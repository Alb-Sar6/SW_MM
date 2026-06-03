package mod.magd.pkgs.core.navigation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import pro.sketchware.utility.FilePathUtil;

import mod.magd.pkgs.core.utils.PkgUtils;


// =========================================================
// PkgFastNav = Package Fast Navigator
// =========================================================

// PURPOSE:
    // Builds a navigation-friendly list of Java/Kotlin packages
    // from the Sketchware project java directory
    // (/storage/emulated/0/.sketchware/data/{projectId}/files/java/).

// DESIGN GOAL:
    // This is NOT a compiler-level package resolver.
    // It is a FAST UI NAVIGATION INDEXER.

// STRATEGY:
    // Traverse directories recursively.
    // Convert folder paths into package names.
    // Include only "useful nodes":
        // Folders containing files.
        // Empty folders (creation targets).
    // Skip "pure container nodes":
        // Folders that contain only subfolders.

// =========================================================

public class PkgFastNav {
    
    
    
    
    
    // =========================================================
    // VARIABLES
    // =========================================================
    private final TreeSet <String> packages = new TreeSet<>(); // TreeSet → auto-sorted, no duplicates, no manual sort needed.
    
    
    
    
    
    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    private PkgFastNav() {}
    
    
    
    
    
    // =========================================================
    // PUBLIC METHODS
    // =========================================================
    
    // MAIN ENTRY POINT
    // Flow:
        // 1) Resolves java root folder from sc_id.
        // 2) Validates it exists and is a directory.
        // 3) Start recursive scan from root.
        // 4) Collect valid package nodes.
        // 5) Return sorted list.
    public List <String> getPackages (String sc_id) {
        File rootDir = FilePathUtil.getDirJava (sc_id);
        errorCheck (rootDir);
        packages.clear();
        scanDirectory (sc_id, rootDir);
        return new ArrayList<> (packages);
    }
    
    
    
    
    // =========================================================
    // PRIVATE METHODS
    // =========================================================
    private void errorCheck (File rootDir) {
        if ( ! rootDir.exists() )
            throw new IllegalArgumentException (
                "Invalid java folder:\n{" + rootDir + "}\nDoesn't exist!"
            );
        
        if ( ! rootDir.isDirectory() ) {
            throw new IllegalArgumentException (
                "Invalid java folder:\n{" + rootDir + "}\nIs not a folder!"
            );
        }
    }
    
    // Recursive directory scanner.
    // INCLUSION RULES:
        // Include folder if:
            // - it contains at least 1 file
            // - OR it is completely empty (useful creation target)
        // Skip folder if:
            // - it contains only subfolders (pure container node)
    private void scanDirectory (String sc_id, File dir) {
        File[] entries = dir.listFiles();
        if (entries == null) return; // Safety check — unreadable directory.
        
        // ======= STEP 1: Analyze Content =======
        
        // If empty
        boolean isEmptyDir = (entries.length == 0);
        if (isEmptyDir) {
            packages.add ( PkgUtils.javaDirToPkg (sc_id, dir) );
            return;
        }
        
        // If not empty, check content (Does it have any files?)
        boolean hasAnyFile = false;
        for (File entry : entries) {
            if ( entry.isFile() ) {
                hasAnyFile = true;
                break;
            }
        }
        if (hasAnyFile) packages.add ( PkgUtils.javaDirToPkg (sc_id, dir) );
        
        
        // ======= STEP 3: RECURSE INTO CHILD FOLDERS =======
        for (File entry : entries) {
            if ( entry.isDirectory() ) scanDirectory (entry);
        }
    }
    
    
    

  
    
}

