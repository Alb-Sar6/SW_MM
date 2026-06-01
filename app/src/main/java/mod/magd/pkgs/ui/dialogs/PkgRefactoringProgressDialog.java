package mod.magd.pkgs.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;

import pro.sketchware.R;
import pro.sketchware.databinding.ProgressMsgBoxBinding;
import mod.magd.pkgs.core.refactoring.PkgRefactoringProgressCallback;


// =========================================================
// PkgRefactoringProgressDialog
// =========================================================

// Non-cancellable dialog shown during package refactoring.
// Displays progress bar, current file being updated, and
// overall percentage complete.

// USAGE:
//     PkgRefactoringProgressDialog dialog = new PkgRefactoringProgressDialog(activity);
//     dialog.setRefactoringInfo("com.old", "com.new");
//     dialog.show();
//
//     // In a background thread, start refactoring with callback:
//     PkgRefactoringProgressCallback callback = (filePath, current, total, percent) -> {
//         dialog.updateProgress(filePath, current, total, percent);
//     };
//     refactoringManager.refactorPackageInAllFiles(..., callback);

// =========================================================

public class PkgRefactoringProgressDialog extends Dialog {

    private Context context;
    private ProgressBar progressBar;
    private TextView tvTitle;
    private TextView tvCurrentFile;
    private TextView tvProgress;
    private TextView tvCancelling;
    private Button btnCancel;
    private Button btnBackground;
    private LinearLayout containerCancelling;

    private String oldPackageName = "";
    private String newPackageName = "";

    private boolean isProcessing = false;
    private boolean isCancelRequested = false;
    private OnCancelCallback onCancelCallback;
    private OnBackgroundCallback onBackgroundCallback;

    private Handler mainHandler = new Handler(Looper.getMainLooper());




    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PkgRefactoringProgressDialog(Context context) {
        super(context);
        this.context = context;
        initializeDialog();
    }




    // =========================================================
    // SETUP
    // =========================================================

    private void initializeDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        // Create custom layout with ProgressMsgBoxBinding or custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(32, 32, 32, 32);

        // Title
        tvTitle = new TextView(context);
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(context.getResources().getColor(android.R.color.black));
        tvTitle.setText("Refactoring Package...");
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.bottomMargin = 16;
        rootLayout.addView(tvTitle, titleParams);

        // Current file being updated
        tvCurrentFile = new TextView(context);
        tvCurrentFile.setTextSize(12);
        tvCurrentFile.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        tvCurrentFile.setText("Preparing...");
        tvCurrentFile.setMaxLines(2);
        LinearLayout.LayoutParams fileParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        fileParams.bottomMargin = 12;
        rootLayout.addView(tvCurrentFile, fileParams);

        // Progress bar
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            8
        );
        pbParams.bottomMargin = 12;
        rootLayout.addView(progressBar, pbParams);

        // Progress percentage text
        tvProgress = new TextView(context);
        tvProgress.setTextSize(12);
        tvProgress.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        tvProgress.setText("0%");
        LinearLayout.LayoutParams progParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        progParams.bottomMargin = 16;
        rootLayout.addView(tvProgress, progParams);

        // Container for cancelling message (initially hidden)
        containerCancelling = new LinearLayout(context);
        containerCancelling.setOrientation(LinearLayout.VERTICAL);
        containerCancelling.setVisibility(View.GONE);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.bottomMargin = 12;
        rootLayout.addView(containerCancelling, containerParams);

        tvCancelling = new TextView(context);
        tvCancelling.setTextSize(12);
        tvCancelling.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        tvCancelling.setText("⏳ Just a moment... cancelling...");
        containerCancelling.addView(tvCancelling, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Buttons container
        LinearLayout buttonsLayout = new LinearLayout(context);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams buttonsParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonsParams.topMargin = 16;
        rootLayout.addView(buttonsLayout, buttonsParams);

        // Cancel button
        btnCancel = new Button(context);
        btnCancel.setText("Cancel");
        btnCancel.setOnClickListener(v -> onCancelPressed());
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        );
        cancelParams.rightMargin = 8;
        buttonsLayout.addView(btnCancel, cancelParams);

        // Background button
        btnBackground = new Button(context);
        btnBackground.setText("Run in Background");
        btnBackground.setOnClickListener(v -> onBackgroundPressed());
        LinearLayout.LayoutParams bgParams = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        );
        buttonsLayout.addView(btnBackground, bgParams);

        setContentView(rootLayout);
    }




    // =========================================================
    // PUBLIC — Setup
    // =========================================================

    /**
     * Set the refactoring info (old and new package names).
     * Call before showing the dialog.
     */
    public void setRefactoringInfo(String oldPkg, String newPkg) {
        this.oldPackageName = oldPkg;
        this.newPackageName = newPkg;
        updateTitle();
    }

    /**
     * Set callback for cancel button press.
     */
    public void setOnCancelCallback(OnCancelCallback callback) {
        this.onCancelCallback = callback;
    }

    /**
     * Set callback for background button press.
     */
    public void setOnBackgroundCallback(OnBackgroundCallback callback) {
        this.onBackgroundCallback = callback;
    }




    // =========================================================
    // PUBLIC — Update Progress
    // =========================================================

    /**
     * Update dialog with current progress.
     * Call this from the refactoring callback.
     */
    public void updateProgress(
        String filePath,
        int currentIndex,
        int totalCount,
        int percentageDone
    ) {
        mainHandler.post(() -> {
            isProcessing = true;
            disableCancelButton();

            // Extract just the filename from the path for display
            String displayPath = extractDisplayPath(filePath);

            tvCurrentFile.setText("Updating " + currentIndex + "/" + totalCount + " ... " + displayPath);
            progressBar.setProgress(Math.min(percentageDone, 100));
            tvProgress.setText(percentageDone + "%");
        });
    }

    /**
     * Call when refactoring is complete.
     */
    public void onRefactoringComplete() {
        mainHandler.post(() -> {
            isProcessing = false;
            progressBar.setProgress(100);
            tvProgress.setText("100%");
            tvCurrentFile.setText("Refactoring complete!");
            enableCancelButton();
        });
    }

    /**
     * Call if refactoring fails.
     */
    public void onRefactoringError(String errorMessage) {
        mainHandler.post(() -> {
            isProcessing = false;
            tvCurrentFile.setText("Error: " + errorMessage);
            enableCancelButton();
        });
    }




    // =========================================================
    // PRIVATE — Button Handlers
    // =========================================================

    private void onCancelPressed() {
        if (isProcessing) {
            // If we're in the middle of processing a file, show message
            isCancelRequested = true;
            disableCancelButton();
            showCancellingMessage();

            if (onCancelCallback != null) {
                onCancelCallback.onCancelRequested();
            }
        } else {
            // If not processing, dismiss immediately
            dismiss();
            if (onCancelCallback != null) {
                onCancelCallback.onCancelCompleted();
            }
        }
    }

    private void onBackgroundPressed() {
        dismiss();
        if (onBackgroundCallback != null) {
            onBackgroundCallback.onRunBackground();
        }
    }




    // =========================================================
    // PRIVATE — UI Updates
    // =========================================================

    private void updateTitle() {
        String title = "Refactoring: " + oldPackageName + " → " + newPackageName;
        tvTitle.setText(title);
    }

    private void disableCancelButton() {
        btnCancel.setEnabled(false);
        btnCancel.setAlpha(0.5f);
    }

    private void enableCancelButton() {
        btnCancel.setEnabled(true);
        btnCancel.setAlpha(1.0f);
    }

    private void showCancellingMessage() {
        containerCancelling.setVisibility(View.VISIBLE);
        tvCancelling.setText("⏳ Just a moment... finishing current file...");
    }

    private String extractDisplayPath(String fullPath) {
        if (fullPath == null || fullPath.isEmpty()) {
            return "..."
        }
        // Get last 50 chars or full path if shorter
        if (fullPath.length() <= 50) {
            return fullPath;
        }
        return "..." + fullPath.substring(fullPath.length() - 47);
    }




    // =========================================================
    // INTERFACES
    // =========================================================

    /**
     * Callback for cancel button.
     */
    public interface OnCancelCallback {
        /**
         * Called when user presses cancel.
         * If currently processing a file, this is called but
         * the dialog stays open waiting for file to finish.
         */
        void onCancelRequested();

        /**
         * Called when cancel is actually completed
         * (file processing finished or was never started).
         */
        void onCancelCompleted();
    }

    /**
     * Callback for "Run in Background" button.
     */
    public interface OnBackgroundCallback {
        /**
         * Called when user presses "Run in Background".
         * Dialog will be dismissed.
         */
        void onRunBackground();
    }



}
