package mod.magd.pkgs.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * PkgItemView: A reusable UI component for displaying package items in lists.
 * 
 * Layout:
 * - CheckBox on the left (for multi-selection scenarios)
 * - Display name in the middle (with fallback to actual package name)
 * - Actual package name below the display name (in smaller/dimmer text)
 * 
 * Styling matches original Sketchware Pro app design.
 */
public class PkgItemView extends LinearLayout {

    private CheckBox checkBox;
    private TextView displayNameTextView;
    private TextView actualPkgNameTextView;
    private LinearLayout contentContainer;

    private String displayName;
    private String actualPkgName;
    private boolean isMainPackage = false;

    public PkgItemView(Context context) {
        super(context);
        init(context);
    }

    public PkgItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PkgItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setPadding(16, 12, 16, 12);

        // Create CheckBox
        checkBox = new CheckBox(context);
        checkBox.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        LayoutParams checkBoxParams = (LayoutParams) checkBox.getLayoutParams();
        checkBoxParams.setMargins(0, 0, 16, 0);
        checkBox.setLayoutParams(checkBoxParams);
        addView(checkBox);

        // Create content container (vertical layout for display name and actual package name)
        contentContainer = new LinearLayout(context);
        contentContainer.setOrientation(VERTICAL);
        contentContainer.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

        // Create Display Name TextView
        displayNameTextView = new TextView(context);
        displayNameTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        displayNameTextView.setTextSize(16);
        displayNameTextView.setTypeface(null, Typeface.NORMAL);
        displayNameTextView.setTextColor(0xFF212121); // Dark gray, matches Sketchware Pro
        contentContainer.addView(displayNameTextView);

        // Create Actual Package Name TextView (smaller, dimmer)
        actualPkgNameTextView = new TextView(context);
        actualPkgNameTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        actualPkgNameTextView.setTextSize(12);
        actualPkgNameTextView.setTypeface(null, Typeface.ITALIC);
        actualPkgNameTextView.setTextColor(0xFF757575); // Medium gray
        LayoutParams actualPkgParams = (LayoutParams) actualPkgNameTextView.getLayoutParams();
        actualPkgParams.setMargins(0, 4, 0, 0);
        actualPkgNameTextView.setLayoutParams(actualPkgParams);
        contentContainer.addView(actualPkgNameTextView);

        addView(contentContainer);
    }

    /**
     * Set package data and update UI
     * 
     * @param displayName Package display name (can be null or empty)
     * @param actualPkgName Actual package name (required)
     * @param isMainPackage Whether this is the main package
     */
    public void setPackageData(@Nullable String displayName, @NonNull String actualPkgName, boolean isMainPackage) {
        this.displayName = displayName;
        this.actualPkgName = actualPkgName;
        this.isMainPackage = isMainPackage;

        updateDisplay();
    }

    /**
     * Update the display based on current data
     */
    private void updateDisplay() {
        // If display name is null or empty, use actual package name
        String displayText = (displayName == null || displayName.trim().isEmpty()) 
            ? actualPkgName 
            : displayName;

        displayNameTextView.setText(displayText);
        actualPkgNameTextView.setText(actualPkgName);

        // If main package, dim the checkbox and make it non-interactive
        if (isMainPackage) {
            checkBox.setEnabled(false);
            checkBox.setAlpha(0.5f);
            displayNameTextView.setTextColor(0xFF9E9E9E); // Dimmer for main package
            actualPkgNameTextView.setTextColor(0xFFBDBDBD); // Dimmer for main package
        } else {
            checkBox.setEnabled(true);
            checkBox.setAlpha(1f);
            displayNameTextView.setTextColor(0xFF212121);
            actualPkgNameTextView.setTextColor(0xFF757575);
        }
    }

    /**
     * Set checkbox checked state
     */
    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    /**
     * Get checkbox checked state
     */
    public boolean isChecked() {
        return checkBox.isChecked();
    }

    /**
     * Set checkbox enabled state
     */
    public void setCheckBoxEnabled(boolean enabled) {
        checkBox.setEnabled(enabled && !isMainPackage);
    }

    /**
     * Get the actual package name
     */
    public String getActualPkgName() {
        return actualPkgName;
    }

    /**
     * Get the display name (or actual package name if display name is empty)
     */
    public String getDisplayName() {
        return (displayName == null || displayName.trim().isEmpty()) 
            ? actualPkgName 
            : displayName;
    }

    /**
     * Check if this is the main package
     */
    public boolean isMainPackage() {
        return isMainPackage;
    }

    /**
     * Set a listener for checkbox changes
     */
    public void setOnCheckChangeListener(CheckBox.OnCheckedChangeListener listener) {
        checkBox.setOnCheckedChangeListener(listener);
    }
}
