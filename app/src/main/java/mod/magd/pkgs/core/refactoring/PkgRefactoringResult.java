package mod.magd.pkgs.core.refactoring;



import java.util.ArrayList;





public static final class PkgRefactoringResult {

    private int totalFilesProcessed = 0;
    private int totalReplacements = 0;
    private ArrayList<String> errors = new ArrayList<>();

    public PkgRefactoringResult() {}

    // ──────────────────────────────────────────────────────
    // Recording results
    // ──────────────────────────────────────────────────────

    void addFileProcessed() {
        totalFilesProcessed++;
    }

    void addReplacements(int count) {
        totalReplacements += count;
    }

    void addError(String error) {
        errors.add(error);
    }

    // ──────────────────────────────────────────────────────
    // Querying results
    // ──────────────────────────────────────────────────────

    public int getTotalFilesProcessed() {
        return totalFilesProcessed;
    }

    public int getTotalReplacements() {
        return totalReplacements;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public ArrayList<String> getErrors() {
        return new ArrayList<>(errors);
    }

    // ──────────────────────────────────────────────────────
    // Summary
    // ──────────────────────────────────────────────────────

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Refactoring Result:\n");
        sb.append("  Files processed: ").append(totalFilesProcessed).append("\n");
        sb.append("  Total replacements: ").append(totalReplacements).append("\n");

        if (!errors.isEmpty()) {
            sb.append("  Errors: ").append(errors.size()).append("\n");
            for (String error : errors) {
                sb.append("    - ").append(error).append("\n");
            }
        }

        return sb.toString();
    }

    public String getErrorSummary() {
        if (errors.isEmpty()) {
            return "No errors";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Refactoring failed with ").append(errors.size()).append(" error(s):\n\n");
        for (int i = 0; i < errors.size(); i++) {
            sb.append(i + 1).append(". ").append(errors.get(i)).append("\n");
        }
        return sb.toString();
    }

  
    
    
    
    
}

