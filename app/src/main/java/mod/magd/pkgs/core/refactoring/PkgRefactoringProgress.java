package mod.magd.pkgs.refactoring;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

/**
 * Tracks the progress of an ongoing package refactoring operation.
 * Saved to disk in case the app crashes/closes during refactoring.
 */
public class PkgRefactoringProgress {

    @SerializedName("reason")
    private String reason; // e.g., "RenamePkg_com.old_To_com.new"

    @SerializedName("oldPackageName")
    private String oldPackageName;

    @SerializedName("newPackageName")
    private String newPackageName;

    @SerializedName("projectId")
    private String projectId;

    @SerializedName("totalFiles")
    private int totalFiles;

    @SerializedName("totalSizeBytes")
    private long totalSizeBytes;

    @SerializedName("updatedFiles")
    private ArrayList<String> updatedFiles; // Paths of files already updated

    @SerializedName("timestamp")
    private long timestamp;

    public PkgRefactoringProgress() {
        this.updatedFiles = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }

    public PkgRefactoringProgress(String reason, String oldPkg, String newPkg, String projectId) {
        this();
        this.reason = reason;
        this.oldPackageName = oldPkg;
        this.newPackageName = newPkg;
        this.projectId = projectId;
    }

    // =========================================================
    // Getters & Setters
    // =========================================================

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOldPackageName() {
        return oldPackageName;
    }

    public void setOldPackageName(String oldPackageName) {
        this.oldPackageName = oldPackageName;
    }

    public String getNewPackageName() {
        return newPackageName;
    }

    public void setNewPackageName(String newPackageName) {
        this.newPackageName = newPackageName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public long getTotalSizeBytes() {
        return totalSizeBytes;
    }

    public void setTotalSizeBytes(long totalSizeBytes) {
        this.totalSizeBytes = totalSizeBytes;
    }

    public ArrayList<String> getUpdatedFiles() {
        return updatedFiles != null ? updatedFiles : new ArrayList<>();
    }

    public void setUpdatedFiles(ArrayList<String> updatedFiles) {
        this.updatedFiles = updatedFiles;
    }

    public void addUpdatedFile(String filePath) {
        if (this.updatedFiles == null) {
            this.updatedFiles = new ArrayList<>();
        }
        if (!this.updatedFiles.contains(filePath)) {
            this.updatedFiles.add(filePath);
        }
    }

    public boolean isFileUpdated(String filePath) {
        return this.updatedFiles != null && this.updatedFiles.contains(filePath);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // =========================================================
    // Utility
    // =========================================================

    /**
     * Calculate progress as a percentage based on file sizes.
     * @return Progress from 0 to 100
     */
    public int getProgressPercentage() {
        if (totalSizeBytes == 0) return 0;

        long completedSize = 0;
        // Note: In real implementation, you'd sum the sizes of updated files
        // For now, we estimate based on number of files
        completedSize = (long) getUpdatedFiles().size() * (totalSizeBytes / Math.max(totalFiles, 1));

        return (int) ((completedSize * 100) / totalSizeBytes);
    }

    /**
     * Get remaining files to update
     */
    public int getRemainingFiles() {
        return totalFiles - getUpdatedFiles().size();
    }
}
