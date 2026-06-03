package pro.sketchware.utility;

import android.os.Environment;

import java.io.File;


// =========================================================
// FilePathUtil
// =========================================================

// NOTE:
        // ALL METHODS IN THIS CLASS HAVE BEEN MADE STATIC.
    // WHY:
        // It improves performance
        // None of these methods use any instance fields or instance state.
        // They are pure utility methods — they only take parameters and return paths.
        // There was never a reason for them to be instance methods.
    // DOES THIS AFFECT EXISTING CODE?
        // No breaking changes.
        // Java allows calling static methods on an instance:
            // new FilePathUtil().getPathJava(sc_id) → still compiles and runs.
        // The only difference is the compiler will now show a warning on those old calls:
            // "Static method should be accessed in a static way"
        // The warning is harmless — just a nudge to clean up the call site.
        // Whenever u get free,
            // replace: new FilePathUtil().method()
            // With: FilePathUtil.method()
        
    // ONCE ALL INSTANCE CALLS ARE REPLCED WITH STATIC CALLS IN THIS PROJECT,
        // UNCOMMENT THE PRIVATE CONSTRUCTOR "private FilePathUtil() {}"
        // TO PREVENT USERS FROM MAKING NEW INSTANCES

// =========================================================


public class FilePathUtil {
    
    
    
    
    
    // =========================================================
    // CONSTANTS
    // =========================================================
    private static final File SKETCHWARE_DATA = new File (Environment.getExternalStorageDirectory(), ".sketchware/data/");
    private static final File SKETCHWARE_LOCAL_LIBS = new File (Environment.getExternalStorageDirectory(), ".sketchware/libs/local_libs");
    
    
    
    
    
    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    // private FilePathUtil() {}
    
    
    
    
    
    // =========================================================
    // PUBLIC METHODS
    // =========================================================
    
    // =========================================================
    // File returns
    // =========================================================
    
    // sc_id = projectId
    public static File getProjectDir (String sc_id) {
        return new File (SKETCHWARE_DATA, sc_id);
    }
    
    public static File getProjectFilesDir (String sc_id) {
        return new File (getProjectDir (sc_id), "files");
    }
    
    public static File getDirJava (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "java");
    }
    
    public static File getLocalLibraryDir (String libraryName) {
        return new File (SKETCHWARE_LOCAL_LIBS, libraryName);
    }
    
    
    
    // =========================================================
    // From Project Data Directory → inside (.sketchware/data/{projectId})
    // =========================================================
    
    // ======= Folders (3) =======
    public static String getPathSvg (String sc_id) {
        return new File (getProjectDir (sc_id), "converted-vectors").getAbsolutePath();
    }
    
    public static String getPathProjectFilesDir (String sc_id) {
        return getProjectFilesDir (sc_id).getAbsolutePath();
    }
    
    // TODO: make method for "Injection"
    
    
    
    // ======= Files (19) =======
    public static String getManifestBroadcast (String sc_id) {
        return new File (getProjectDir (sc_id), "broadcast").getAbsolutePath();
    }
    
    // TODO: make method for "build_config"
    
    // TODO: make method for "command"
    
    public static String getLastCompileLogPath (String sc_id) {
        return new File (getProjectDir (sc_id), "compile_log").getAbsolutePath();
    }
    
    // TODO: make method for "file"
    
    public static String getPathImport (String sc_id) {
        return new File (getProjectDir (sc_id), "import").getAbsolutePath();
    }
    
    public static String getManifestJava (String sc_id) {
        return new File (getProjectDir (sc_id), "java").getAbsolutePath();
    }
    
    // TODO: make method for "library"
    
    public static String getPathLocalLibrary (String sc_id) {
        return new File (getProjectDir (sc_id), "local_library").getAbsolutePath();
    }
    
    // TODO: make method for "logic"
    
    public static String getPathPermission (String sc_id) {
        return new File (getProjectDir (sc_id), "permission").getAbsolutePath();
    }
    
    // TODO: make method for "proguard"
    
    public static String getPathProguard (String sc_id) {
        return new File (getProjectDir (sc_id), "proguard-rules.pro").getAbsolutePath();
    }
    
    // TODO: make method for "project_config"
    
    // TODO: make method for "resource"
    
    public static String getManifestService (String sc_id) {
        return new File (getProjectDir (sc_id), "service").getAbsolutePath();
    }
    
    // TODO: make method for "stringfog"
    
    // TODO: make method for "view"
    
    // TODO: make method for "view_root"
    
    
    
    // =========================================================
    // inside (.sketchware/data/{projectId}/converted-vectors)
    // =========================================================
    public static String getSvgFullPath (String sc_id, String resName) {
        return new File (getPathSvg (sc_id), resName + ".svg").getAbsolutePath();
    }
    
    
    
    // =========================================================
    // From Project Data Files Directory → inside (.sketchware/data/{projectId}/files)
    // =========================================================
    
    // ======= Folders (10) =======
    public static String getPathAssets (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "assets").getAbsolutePath();
    }
    
    public static String getPathBroadcast (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "broadcast").getAbsolutePath();
    }
    
    public static String getPathJava (String sc_id) {
        return getDirJava (sc_id).getAbsolutePath();
    }
    
    public static String getPathKotlinCompilerPlugins (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "kt_plugins").getAbsolutePath();
    }
    
    public static String getJarPathLocalLibraryUser (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "library" + File.separator + "jar").getAbsolutePath();
    }
    
    public static String getDexPathLocalLibraryUser (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "library" + File.separator + "dex").getAbsolutePath();
    }
    
    public static String getResPathLocalLibraryUser (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "library" + File.separator + "res").getAbsolutePath();
    }
    
    public static String getPathNativelibs (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "native_libs").getAbsolutePath();
    }
    
    public static String getPathResource (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "resource").getAbsolutePath();
    }
    
    public static String getPathService (String sc_id) {
        return new File (getProjectFilesDir (sc_id), "service").getAbsolutePath();
    }
    
    
    
    // =========================================================
    // SKETCHWARE_LOCAL_LIBS → inside (.sketchware/libs/local_libs)
    // =========================================================
    public static String getJarPathLocalLibrary (String libraryName) {
        return new File (getLocalLibraryDir (libraryName), "classes.jar").getAbsolutePath();
    }
    
    public static String getDexPathLocalLibrary (String libraryName) {
        return new File (getLocalLibraryDir (libraryName), "classes.dex").getAbsolutePath();
    }
    
    public static String getResPathLocalLibrary (String libraryName) {
        return new File (getLocalLibraryDir (libraryName), "res").getAbsolutePath();
    }
    
    
    
    
    
}


