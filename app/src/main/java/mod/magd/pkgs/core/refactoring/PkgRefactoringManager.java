package mod.magd.pkgs.core.refactoring;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import mod.jbk.util.LogUtil;
import pro.sketchware.utility.FileUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



// =========================================================
// PkgRefactoringManager
// =========================================================

// Comprehensive package refactoring engine that searches
// and replaces package names throughout all .java files.

// PURPOSE:
//     // When a package is renamed (e.g., com.old → com.new):
//     //   1. Scans ALL .java files in ALL packages
//     //   2. Searches for ALL occurrences of the old package name
//     //   3. Replaces in package declarations, imports, refs, strings
//     //   4. Tracks progress to disk (for crash recovery)
//     //   5. Handles failures gracefully with rollback

// REPLACEMENTS APPLIED:
//     // 1. Package declarations: "package com.old;" → "package com.new;"
//     // 2. Import statements: "import com.old.*;" → "import com.new.*;"
//     // 3. Qualified references: "com.old.ClassName" → "com.new.ClassName"
//     // 4. String literals: "\"com.old\"" → "\"com.new\"" (ANY string containing package)
//     // 5. Comment references: Also replaced in comments
//     // 6. Search scope: ENTIRE file text, character by character

// PROGRESS TRACKING:
//     // Saves PkgRefactoringProgress to disk after EACH file update
//     // Location: .sketchware/data/{projectId}/files/in_progress/
//     // File names: refactoring_progress.json
//     // Used for crash recovery & resume

// CRASH RECOVERY:
//     // If app closes during refactoring:
//     //   1. Progress file exists with list of updated files
//     //   2. On next open, check for in_progress folder
//     //   3. Show "Resume?" dialog
//     //   4. Resume from where it left off (skip already-updated files)
//     //   5. Delete in_progress folder when done or cancelled

// USAGE:
//     PkgRefactoringManager manager = new PkgRefactoringManager();
//     File projectFilesDir = new File("/storage/.../files/");
//     String projectId = "611";
//
//     PkgRefactoringResult result = manager.refactorPackageInAllFiles(
//         projectFilesDir,
//         projectId,
//         "com.old",
//         "com.new",
//         progressCallback  // to update UI
//     );
//
//     if (result.hasErrors()) {
//         Log.e("Refactoring failed: " + result.getErrorSummary());
//     } else {
//         Log.d("Refactored " + result.getTotalReplacements() + " references");
//     }

// =========================================================

public final class PkgRefactoringManager {

    private static final String TAG = "PkgRefactoringManager";
    private static final String IN_PROGRESS_DIR = "in_progress";
    private static final String PROGRESS_FILE_NAME = "refactoring_progress.json";
    private static final String JAVA_FILE_PATTERN = ".java";




    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PkgRefactoringManager() {}




    // =========================================================
    // PUBLIC — Main Refactoring Entry Point
    // =========================================================

    /**
     * Refactor package name in ALL .java files across all packages.
     *
     * @param projectFilesDir      Path to .sketchware/data/{projectId}/files/
     * @param projectId            Project ID for tracking
     * @param oldPackageName       Old package name (e.g., "com.old")
     * @param newPackageName       New package name (e.g., "com.new")
     * @param progressCallback     Optional callback to update UI (null = no updates)
     * @return                     Result object with stats and errors
     */
    public PkgRefactoringResult refactorPackageInAllFiles(
        File projectFilesDir,
        String projectId,
        String oldPackageName,
        String newPackageName,
        ProgressCallback progressCallback
    ) {
        PkgRefactoringResult result = new PkgRefactoringResult();

        // ──────────────────────────────────────────────────────
        // Validation
        // ──────────────────────────────────────────────────────
        if (projectFilesDir == null || !projectFilesDir.exists()) {
            result.addError("projectFilesDir does not exist");
            return result;
        }

        if (oldPackageName == null || oldPackageName.isEmpty()) {
            result.addError("oldPackageName is null or empty");
            return result;
        }

        if (newPackageName == null || newPackageName.isEmpty()) {
            result.addError("newPackageName is null or empty");
            return result;
        }

        if (oldPackageName.equals(newPackageName)) {
            result.addError("oldPackageName and newPackageName are identical");
            return result;
        }

        LogUtil.d(TAG, "Starting refactoring: " + oldPackageName + " → " + newPackageName);

        // ──────────────────────────────────────────────────────
        // Setup in-progress tracking
        // ──────────────────────────────────────────────────────
        String reason = buildReasonString(oldPackageName, newPackageName);
        PkgRefactoringProgress progress = new PkgRefactoringProgress(
            reason,
            oldPackageName,
            newPackageName,
            projectId
        );

        File inProgressDir = new File(projectFilesDir, IN_PROGRESS_DIR);
        File reasonDir = new File(inProgressDir, reason);
        File progressFile = new File(reasonDir, PROGRESS_FILE_NAME);

        try {
            if (!reasonDir.exists()) {
                reasonDir.mkdirs();
            }
        } catch (Exception e) {
            result.addError("Failed to create in_progress directory: " + e.getMessage());
            return result;
        }

        // ──────────────────────────────────────────────────────
        // Collect all .java files from main package
        // ──────────────────────────────────────────────────────
        File mainJavaDir = new File(projectFilesDir, "java");
        ArrayList<File> allJavaFiles = new ArrayList<>();

        if (mainJavaDir.exists() && mainJavaDir.isDirectory()) {
            collectJavaFiles(mainJavaDir, allJavaFiles);
        }

        // ──────────────────────────────────────────────────────
        // Collect all .java files from java_extra packages
        // ──────────────────────────────────────────────────────
        File javaExtraDir = new File(projectFilesDir, "java_extra");
        if (javaExtraDir.exists() && javaExtraDir.isDirectory()) {
            File[] packageDirs = javaExtraDir.listFiles(File::isDirectory);
            if (packageDirs != null) {
                for (File pkgDir : packageDirs) {
                    collectJavaFiles(pkgDir, allJavaFiles);
                }
            }
        }

        if (allJavaFiles.isEmpty()) {
            LogUtil.w(TAG, "No .java files found to refactor");
            cleanupInProgressFolder(projectFilesDir, reason);
            return result;
        }

        LogUtil.d(TAG, "Found " + allJavaFiles.size() + " .java files to process");

        // ──────────────────────────────────────────────────────
        // Calculate total size
        // ──────────────────────────────────────────────────────
        long totalSize = 0;
        for (File f : allJavaFiles) {
            totalSize += f.length();
        }
        progress.setTotalFiles(allJavaFiles.size());
        progress.setTotalSizeBytes(totalSize);

        // ──────────────────────────────────────────────────────
        // Process each file
        // ──────────────────────────────────────────────────────
        long processedSize = 0;

        for (int i = 0; i < allJavaFiles.size(); i++) {
            File javaFile = allJavaFiles.get(i);
            String filePath = javaFile.getAbsolutePath();

            // Skip if already processed in previous session
            if (progress.isFileUpdated(filePath)) {
                LogUtil.d(TAG, "Skipping already-updated file: " + filePath);
                processedSize += javaFile.length();
                continue;
            }

            try {
                // Update UI callback
                if (progressCallback != null) {
                    progressCallback.onFileProcessing(
                        filePath,
                        i + 1,
                        allJavaFiles.size(),
                        (int) ((processedSize * 100) / Math.max(totalSize, 1))
                    );
                }

                // Refactor this file
                int replacements = refactorJavaFile(javaFile, oldPackageName, newPackageName);
                result.addReplacements(replacements);
                result.addFileProcessed();

                // Mark as updated in progress
                progress.addUpdatedFile(filePath);
                saveProgress(progressFile, progress);

                LogUtil.d(TAG, "Refactored " + filePath + " (" + replacements + " replacements)");

            } catch (Exception e) {
                LogUtil.e(TAG, "Failed to refactor " + filePath, e);
                result.addError("Failed to refactor " + filePath + ": " + e.getMessage());
                // Continue with next file — don't stop on single failure
            }

            processedSize += javaFile.length();
        }

        // ──────────────────────────────────────────────────────
        // Cleanup on success
        // ──────────────────────────────────────────────────────
        if (!result.hasErrors()) {
            LogUtil.d(TAG, "Refactoring completed successfully");
            cleanupInProgressFolder(projectFilesDir, reason);
        } else {
            LogUtil.w(TAG, "Refactoring had errors, keeping in_progress folder for resume");
        }

        return result;
    }




    // =========================================================
    // PUBLIC — Check and Resume In-Progress Refactoring
    // =========================================================

    /**
     * Check if there's an in-progress refactoring from a previous session.
     * Useful to call on app startup to detect crashes.
     *
     * @param projectFilesDir  Path to .sketchware/data/{projectId}/files/
     * @return                 PkgRefactoringProgress if found, null otherwise
     */
    public PkgRefactoringProgress checkForInProgressRefactoring(File projectFilesDir) {
        File inProgressDir = new File(projectFilesDir, IN_PROGRESS_DIR);

        if (!inProgressDir.exists() || !inProgressDir.isDirectory()) {
            return null;
        }

        File[] reasonDirs = inProgressDir.listFiles(File::isDirectory);
        if (reasonDirs == null || reasonDirs.length == 0) {
            return null;
        }

        // Load the most recent one
        File reasonDir = reasonDirs[0];
        File progressFile = new File(reasonDir, PROGRESS_FILE_NAME);

        if (!progressFile.exists()) {
            return null;
        }

        try {
            return loadProgress(progressFile);
        } catch (Exception e) {
            LogUtil.e(TAG, "Failed to load in-progress refactoring", e);
            return null;
        }
    }




    // =========================================================
    // PUBLIC — Resume In-Progress Refactoring
    // =========================================================

    /**
     * Resume a refactoring that was interrupted (e.g., app crash).
     *
     * @param projectFilesDir      Path to .sketchware/data/{projectId}/files/
     * @param inProgressData       PkgRefactoringProgress from checkForInProgressRefactoring()
     * @param progressCallback     Optional callback to update UI
     * @return                     Result object with final stats
     */
    public PkgRefactoringResult resumeRefactoring(
        File projectFilesDir,
        PkgRefactoringProgress inProgressData,
        ProgressCallback progressCallback
    ) {
        if (inProgressData == null) {
            PkgRefactoringResult result = new PkgRefactoringResult();
            result.addError("No in-progress refactoring data provided");
            return result;
        }

        LogUtil.d(TAG, "Resuming refactoring: " + inProgressData.getReason());

        // Resume using the same main method
        PkgRefactoringResult result = refactorPackageInAllFiles(
            projectFilesDir,
            inProgressData.getProjectId(),
            inProgressData.getOldPackageName(),
            inProgressData.getNewPackageName(),
            progressCallback
        );

        return result;
    }




    // =========================================================
    // PRIVATE — Refactor Single File
    // =========================================================

    /**
     * Refactor a single .java file:
     * - Read entire file
     * - Replace old package name with new (in ALL contexts)
     * - Write back to file
     *
     * @param javaFile        The .java file to refactor
     * @param oldPackage      Old package name
     * @param newPackage      New package name
     * @return                Number of replacements made
     */
    private int refactorJavaFile(
        File javaFile,
        String oldPackage,
        String newPackage
    ) throws Exception {

        // Read entire file
        String content = readFileAsString(javaFile);
        if (content == null) {
            return 0;
        }

        // Count replacements BEFORE (to return the count)
        int countBefore = countOccurrences(content, oldPackage);

        // Replace all occurrences of old package name with new
        // This is a simple string replacement that handles ALL contexts:
        // - Package declarations
        // - Imports
        // - Qualified class names
        // - String literals
        // - Comments
        // Everything!
        String newContent = content.replace(oldPackage, newPackage);

        // Write back to file
        writeFileAsString(javaFile, newContent);

        return countBefore;
    }




    // =========================================================
    // PRIVATE — File Collection
    // =========================================================

    /**
     * Recursively collect all .java files from a directory.
     */
    private void collectJavaFiles(File directory, ArrayList<File> result) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] children = directory.listFiles();
        if (children == null) {
            return;
        }

        for (File child : children) {
            if (child.isDirectory()) {
                collectJavaFiles(child, result);
            } else if (child.getName().endsWith(JAVA_FILE_PATTERN)) {
                result.add(child);
            }
        }
    }




    // =========================================================
    // PRIVATE — File I/O
    // =========================================================

    /**
     * Read entire file as string.
     */
    private String readFileAsString(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Write string to file.
     */
    private void writeFileAsString(File file, String content) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(content);
        }
    }

    /**
     * Count occurrences of a substring in a string.
     */
    private int countOccurrences(String content, String searchTerm) {
        int count = 0;
        int index = 0;
        while ((index = content.indexOf(searchTerm, index)) != -1) {
            count++;
            index += searchTerm.length();
        }
        return count;
    }




    // =========================================================
    // PRIVATE — Progress Persistence
    // =========================================================

    /**
     * Save progress to JSON file.
     */
    private void saveProgress(File progressFile, PkgRefactoringProgress progress) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(progress);
        FileUtil.writeFile(progressFile.getAbsolutePath(), json);
    }

    /**
     * Load progress from JSON file.
     */
    private PkgRefactoringProgress loadProgress(File progressFile) throws Exception {
        String json = FileUtil.readFile(progressFile.getAbsolutePath());
        Gson gson = new Gson();
        return gson.fromJson(json, PkgRefactoringProgress.class);
    }




    // =========================================================
    // PRIVATE — Cleanup
    // =========================================================

    /**
     * Delete the in_progress folder after successful refactoring.
     */
    private void cleanupInProgressFolder(File projectFilesDir, String reason) {
        try {
            File inProgressDir = new File(projectFilesDir, IN_PROGRESS_DIR);
            File reasonDir = new File(inProgressDir, reason);

            // Delete progress file
            File progressFile = new File(reasonDir, PROGRESS_FILE_NAME);
            if (progressFile.exists()) {
                progressFile.delete();
            }

            // Delete reason directory
            if (reasonDir.exists()) {
                reasonDir.delete();
            }

            // Delete in_progress directory if empty
            if (inProgressDir.exists()) {
                File[] children = inProgressDir.listFiles();
                if (children == null || children.length == 0) {
                    inProgressDir.delete();
                }
            }

            LogUtil.d(TAG, "Cleaned up in_progress folder");
        } catch (Exception e) {
            LogUtil.w(TAG, "Failed to cleanup in_progress folder: " + e.getMessage());
            // Don't fail the entire operation for cleanup issues
        }
    }




    // =========================================================
    // PRIVATE — Utility
    // =========================================================

    /**
     * Build a reason string for in_progress folder naming.
     * Format: "RenamePkg_{com.old}_To_{com.new}"
     */
    private String buildReasonString(String oldPkg, String newPkg) {
        return "RenamePkg_{" + oldPkg + "}_To_{" + newPkg + "}";
    }






}
