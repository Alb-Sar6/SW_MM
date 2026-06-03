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

