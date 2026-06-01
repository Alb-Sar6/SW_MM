# SW Multi-Package Support

## 🎯 Project Goal

**Enable Sketchware Pro users to build Android projects with multiple Java/Kotlin packages**,
    going beyond the original single-package limitation.
    This allows developers to organize their projects into logical package structures
    (e.g., `app.main.ui`, `app.data`, `app.service`),
    improving code organization and maintainability.

---

## 📱 User Flow / How It Works

### The Complete User Experience

1) **User opens a Sketchware Pro project**
   - Project loads normally with all existing functionality

2) **User taps the 3-dot menu (options menu) at the top right**
    - Opens the project configuration menu/drawer

3) **User selects "Java/Kotlin Manager" from the configuration drawer**
    - Opens the Java/Kotlin file browser interface
    - `ManageJavaActivity` initializes:
        - Loads `PkgRegistry` from `java_pkgs.json`
        - Runs `PkgMigrator` for backward compatibility
        - Sets active package to main package

4) **User sees a package header at the top**
    - Displays the **Main Package** (the original package)
    - Shows two buttons: **"Switch"** and **"Manage"**

    A) ***User taps "Switch"***
        - Shows `PkgPickerDialog`
        - Displays a list of all existing packages
        - Each item shows:
            - Display name (e.g., "UI Layer") in bold
            - Package name (e.g., "com.example.app.ui") in gray
            - Blue checkmark if this is the current active package
        - User clicks a package to select it
        - `ManageJavaActivity` updates:
            - Active package switches
            - File browser refreshes to show files from the chosen package
            - `PkgView` (header) updates to reflect new active package
    
    B) ***User taps "Manage"***
        - Shows `PkgManageDialog`
        - Displays title: "What would you like to do?"
        - Shows three buttons: **Create**, **Edit**, **Delete**



            1. ****User taps "Create"****
                - `PkgCreatorDialog` appears
                - User enters:
                    - **Package Name** (e.g., `com.example.app.ui`)
                        - Fully-qualified Java package name
                        - Validated by `PkgValidator` (8 validation rules)
                    - **Display Name** (e.g., `UI Layer`)
                        - Human-readable label for the UI
                        - Max 40 characters
                        - Can be empty/null (falls back to package name in UI)
                - Package is created:
                    - Main package files saved at: `/storage/emulated/0/.sketchware/data/{projectId}/files/java/`
                    - Extra packages files saved at: `/storage/emulated/0/.sketchware/data/{projectId}/files/java_extra/{PackageName}/`
                    - Package registry entry saved at: `/storage/emulated/0/.sketchware/data/{projectId}/files/java_pkgs.json`
                - Dialog closes and `ManageJavaActivity` refreshes
                - New package immediately available for switching/use

            
            2. ****User taps "Edit"****
                - `PkgEditDialog` appears with two phases:
                    - **Phase 1**: Shows list of all packages
                    - User selects a package to edit
                    - **Phase 2**: Shows edit form with: (we could reuse the PkgCreatorDialog here)
                        - Display name field
                        - Package name field
                        - User edits one or both fields
                        - Validation applied via `PkgValidator`
                - *****If package name changed:*****
                    - Shows warning: "Package name changed. Files need to be updated."
                    - Three options:
                        - **Cancel** - Don't apply any changes
                        - **Don't update files** - Update package entry and rename folder only
                        - **Update files** - Full refactoring:
                            - Updates `java_pkgs.json` registry entry
                            - Renames package folder in `java_extra/`
                            - Loops through ALL files in ALL packages (main + all extras)
                            - Uses `PkgRefactoringManager` to search/replace old package name with new
                            - Updates:
                                - Package declarations
                                - Import statements
                                - Fully-qualified references
                                - String literals containing package name
                                - Just EVERYTHING .. IT SEARCHES THE ENTIRE FILE TEXT
                - *****If only display name changed:*****
                    - Just updates registry entry (no file changes needed)
                - Dialog closes and refreshes

            
            3. ****User taps "Delete"****
                - `PkgDeleteDialog` appears with:
                    - Title: "Delete Packages"
                    - List of all packages with checkboxes
                    - Main package checkbox is grayed out (cannot delete)
                - User checks the packages to delete
                - Multiple packages can be selected
                - User taps "Delete" button
                - Confirmation shown with list of packages to be deleted
                - User confirms by tapping "Delete" again
                - Packages are deleted:
                    - Entries removed from `java_pkgs.json`
                    - Package folders deleted from `java_extra/`
                    - If active package was deleted → automatically switches to main package
                - `ManageJavaActivity` refreshes
                - File browser now shows main package files

---

## ✅ This Code Also Handles

### Sync, Backups & Exporting

1) **Package Synchronization (Auto-sync on Open & Before Compile)**
    - Runs at:
        - When opening `ManageJavaActivity`
        - Before compiling project
        - On-demand sync operations
    - `PkgRegistrySync` does:
        - **Scan `java_extra/` directory:**
            - If package folder exists but NOT in `java_pkgs.json`:
                - Validates package name with `PkgValidator`
                    - If valid: automatically registers in `java_pkgs.json` with empty display name & pkg name is the folder name
                    - If invalid: throws compile error (user must fix manually or delete folder)
        - **Scan `java_pkgs.json` entries:**
            - If package in registry but folder NOT in `java_extra/`:
                - Creates empty folder for that package (preserves registry entry)
    - Result: Registry always matches file system state

2) **When compiling the project**
    - Before compilation, sync runs (ensures all packages are registered)
    - `ProjectBuilderBridge.collectAllJavaSourceFiles()` called
    - Collects ALL `.java` and `.kt` files from:
        - Main package: `files/java/`
        - All extra packages: `files/java_extra/{packageName}/`
    - All files passed to Eclipse ECJ compiler
    - All packages compile together in one build
    - All packages can reference each other normally
    - Final APK includes all compiled bytecode from all packages

3) **When backing up the project**
    - `BackupFactory` creates backup zip including:
        - All project data from `files/` → (Sketchware Pro already do this) → (NEEDS DOUBLE CHECK)
        - `java_pkgs.json` registry file (NEW)
        - All package files from `java_extra/` (NEW)
        - Everything needed to restore exact multi-package structure
    - Backup file: `.sketchware/backups/{projectName}/{projectName} v{version} ({projectMainPackageName}, {version}) {date}T{randomNumber}.swb` → (NEEDS DOUBLE CHECK)

4) **When restoring from backup** → (Sketchware Pro already do this) → (NEEDS DOUBLE CHECK)
    - `BackupRestoreManager` restores:
        - All files from `java/` (main package)
        - All files from `java_extra/` with package folders (NEW)
        - `java_pkgs.json` registry (NEW)
        - All other project data
    - Project restored to exact state when backed up

5) **When exporting the project**
    - Exporting, is made to export the source code to Android studio
    - it suppose to makes the correct code structure 
    - it already puts the main pkg at the correct place
    - but we need to add the extra pkgs too.
    - `ExportProjectActivity` exports including:
        - All source code from `java/`
        - All source code from `java_extra/`
    - Export file contains full multi-package structure
    - don't need `java_pkgs.json` registry in the export

---
