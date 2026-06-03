package pro.sketchware.utility;

import android.os.Environment;

import java.io.File;

public class FilePathUtil {
    
    private static final File SKETCHWARE_DATA = new File(Environment.getExternalStorageDirectory(), ".sketchware/data/");
    private static final File SKETCHWARE_LOCAL_LIBS = new File(Environment.getExternalStorageDirectory(), ".sketchware/libs/local_libs");
    
    
    
    // =========================================================
    // File returns
    // =========================================================
    
    // sc_id = projectId
    public static File getProjectDir(String sc_id) {
        return new File(SKETCHWARE_DATA, sc_id);
    }
    
    public static File getProjectFilesDir(String sc_id) {
        return new File(getProjectDir(sc_id), "files");
    }
    
    public static File getDirJava(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "java");
    }
    
    public static File getLocalLibraryDir(String libraryName) {
        return new File(SKETCHWARE_LOCAL_LIBS, libraryName);
    }
    
    
    
    // =========================================================
    // From Project Data Directory → inside (.sketchware/data/{projectId})
    // =========================================================
    
    // ======= Folders (3) =======
    public String getPathSvg(String sc_id) {
        return new File(getProjectDir(sc_id), "converted-vectors").getAbsolutePath();
    }
    
    public String getPathProjectFilesDir(String sc_id) {
        return getProjectFilesDir(sc_id).getAbsolutePath();
    }
    
    // TODO: make method Injection
    
    
    
    // ======= Files (19) =======
    public String getManifestBroadcast(String sc_id) {
        return new File(getProjectDir(sc_id), "broadcast").getAbsolutePath();
    }
    
    // TODO: make method build_config
    
    // TODO: make method command
    
    public static String getLastCompileLogPath(String sc_id) {
        return new File(getProjectDir(sc_id), "compile_log").getAbsolutePath();
    }
    
    // TODO: make method file
    
    public String getPathImport(String sc_id) {
        return new File(getProjectDir(sc_id), "import").getAbsolutePath();
    }
    
    public String getManifestJava(String sc_id) {
        return new File(getProjectDir(sc_id), "java").getAbsolutePath();
    }
    
    // TODO: make method library
    
    public String getPathLocalLibrary(String sc_id) {
        return new File(getProjectDir(sc_id), "local_library").getAbsolutePath();
    }
    
    // TODO: make method logic
    
    public String getPathPermission(String sc_id) {
        return new File(getProjectDir(sc_id), "permission").getAbsolutePath();
    }
    
    // TODO: make method proguard
    
    public String getPathProguard(String sc_id) {
        return new File(getProjectDir(sc_id), "proguard-rules.pro").getAbsolutePath();
    }
    
    // TODO: make method project_config
    
    // TODO: make method resource
    
    public String getManifestService(String sc_id) {
        return new File(getProjectDir(sc_id), "service").getAbsolutePath();
    }
    
    // TODO: make method stringfog
    
    // TODO: make method view
    
    // TODO: make method view_root
    
    
    
    // =========================================================
    // inside (.sketchware/data/{projectId}/converted-vectors)
    // =========================================================
    public String getSvgFullPath(String sc_id, String resName) {
        return new File(getPathSvg(sc_id) + File.separator + resName + ".svg").getAbsolutePath();
    }
    
    
    
    // =========================================================
    // From Project Data Files Directory → inside (.sketchware/data/{projectId}/files)
    // =========================================================
    
    // ======= Folders (10) =======
    public String getPathAssets(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "assets").getAbsolutePath();
    }
    
    public String getPathBroadcast(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "broadcast").getAbsolutePath();
    }
    
    public String getPathJava(String sc_id) {
        return getDirJava(sc_id).getAbsolutePath();
    }
    
    public String getPathKotlinCompilerPlugins(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "kt_plugins").getAbsolutePath();
    }
    
    public String getJarPathLocalLibraryUser(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "library" + File.separator + "jar").getAbsolutePath();
    }
    
    public String getDexPathLocalLibraryUser(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "library" + File.separator + "dex").getAbsolutePath();
    }
    
    public String getResPathLocalLibraryUser(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "library" + File.separator + "res").getAbsolutePath();
    }
    
    public String getPathNativelibs(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "native_libs").getAbsolutePath();
    }
    
    public String getPathResource(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "resource").getAbsolutePath();
    }
    
    public String getPathService(String sc_id) {
        return new File(getProjectFilesDir(sc_id), "service").getAbsolutePath();
    }
    
    
    
    // =========================================================
    // SKETCHWARE_LOCAL_LIBS → inside (.sketchware/libs/local_libs)
    // =========================================================
    public String getJarPathLocalLibrary(String libraryName) {
        return new File(getLocalLibraryDir(libraryName), "classes.jar").getAbsolutePath();
    }
    
    public String getDexPathLocalLibrary(String libraryName) {
        return new File(getLocalLibraryDir(libraryName), "classes.dex").getAbsolutePath();
    }
    
    public String getResPathLocalLibrary(String libraryName) {
        return new File(getLocalLibraryDir(libraryName), "res").getAbsolutePath();
    }
        
        
        
}


