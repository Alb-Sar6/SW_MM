package mod.magd.pkgs.core.refactoring;
/**
 * Callback to report progress to the UI.
 * Implement to update progress dialogs, etc.
 */
public interface PkgRefactoringProgressCallback {
    /**
     * Called for each file being processed.
     *
     * @param filePath      Absolute path of current file
     * @param currentIndex  1-based index (e.g., 5 out of 23)
     * @param totalCount    Total files to process
     * @param percentageDone  Overall percentage done (0-100)
     */
    void onFileProcessing(String filePath, int currentIndex, int totalCount, int percentageDone);
}
