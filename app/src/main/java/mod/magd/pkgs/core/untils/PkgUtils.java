package mod.magd.pkgs.core.utils;

import java.io.File;

import pro.sketchware.utility.FilePathUtil;


// =========================================================
// PkgUtils = Package Utilities
// =========================================================

// PURPOSE:
    // Converts file system paths into Java package names.

// =========================================================

public class PkgUtils {
    
    
    
    
    
    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    private PkgUtils() {}
    
    
    
    
    
    // =========================================================
    // PUBLIC METHODS
    // =========================================================
    
    // Converts a relative path to a package name.
    // Example:
        // "com/magd/ui" → "com.magd.ui"
    public static String toPkg (String path) {
        if ( path == null || path.isEmpty() ) return "";
        return (
            path
                .replace (File.separatorChar, '.')
                .replace ('/', '.')
                .replaceAll ("^\\.+|\\.+$", "") // Trim leading/trailing dots.
        );
    }
    
    // File f = new File ("com/magd/ui");
        // f.getPath() → "com/magd/ui" → returns exactly what you gave it
        // f.getAbsolutePath() → "/storage/emulated/0/.sketchware/data/{projectId}/files/java/com/magd/ui" → returns the actual working directory
    public static String toPkg (File dir) {
        return toPkg ( dir.getPath() );
    }
    
    
    
    // Strips the java root prefix then converts to package name.
    // Example:
        // /storage/emulated/0/.sketchware/data/{projectId}/files/java/com/magd/ui" → "com.magd.ui"
    public static String javaDirToPkg (String sc_id, String path) {
        String javaRootPath = FilePathUtil.getPathJava (sc_id);
        if ( ! path.startsWith (javaRootPath) ) return toPkg (path);
        
        String relative = path.substring ( javaRootPath.length() );
        return toPkg (relative);
    }
    
    public static String javaDirToPkg (String sc_id, File dir) {
        return javaDirToPkg ( sc_id, dir.getAbsolutePath() );
    }
    
    
    

  
}

