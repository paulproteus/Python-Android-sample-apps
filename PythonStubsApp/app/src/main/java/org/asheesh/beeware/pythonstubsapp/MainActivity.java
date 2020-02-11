package org.asheesh.beeware.pythonstubsapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import kotlin.TypeCastException;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref.IntRef;
import kotlin.text.Charsets;

import org.beeware.rubicon.Python;
import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity {
    private static IPythonApp pythonApp;

    private String getPythonBasePath() throws Throwable {
        return this.getPythonBaseDir().getAbsolutePath();
    }

    private File getPythonBaseDir() throws Throwable {
        StringBuilder var10002 = new StringBuilder();
        Context var10003 = this.getApplicationContext();
        Intrinsics.checkExpressionValueIsNotNull(var10003, "applicationContext");
        File var3 = var10003.getFilesDir();
        if (var3 == null) {
            Intrinsics.throwNpe();
        }

        File dir = new File(var10002.append(var3.getAbsolutePath()).append("/python/").toString());
        if (!dir.exists()) {
            boolean mkdirResult = dir.mkdirs();
            if (!mkdirResult) {
                throw (Throwable) (new Exception("Unable to find a place to store the Python stdlib."));
            }
        }

        return dir;
    }

    private final void unzipTo(ZipInputStream inputStream, File outputDir) throws Throwable {
        if (outputDir.exists()) {
            Log.d("unpackPython", "deleting recursively");
            FilesKt.deleteRecursively(outputDir);
            Log.d("unpackPython", "deleting recursively done");
        }

        if (!outputDir.mkdirs()) {
            throw (Throwable) (new RuntimeException("Unable to mkdir " + outputDir.getAbsolutePath()));
        } else {
            ZipEntry zipEntry = inputStream.getNextEntry();
            byte[] buf = new byte[4194304];

            while (true) {
                while (zipEntry != null) {
                    File outputFile = new File(outputDir.getAbsolutePath() + '/' + zipEntry);
                    if (zipEntry.isDirectory()) {
                        Log.d("unzipTo", "creating dir " + outputFile.getAbsolutePath());
                        boolean result = outputFile.mkdirs();
                        if (!result) {
                            Log.d("unzipTo", "mkdirs result = " + result);
                        }

                        zipEntry = inputStream.getNextEntry();
                    } else {
                        Log.d("unzipTo", "about to create file " + outputFile.getAbsolutePath());
                        FileOutputStream fos = new FileOutputStream(outputFile.getAbsolutePath());
                        IntRef len = new IntRef();

                        while (true) {
                            int var8 = inputStream.read(buf);
                            len.element = var8;
                            if (var8 <= 0) {
                                fos.close();
                                zipEntry = inputStream.getNextEntry();
                                break;
                            }

                            fos.write(buf, 0, len.element);
                        }
                    }
                }

                inputStream.closeEntry();
                inputStream.close();
                return;
            }
        }
    }

    private final void unpackPython() throws Throwable {
        String myAbi = Build.SUPPORTED_ABIS[0];
        Log.e("unpackPython", "abi is " + myAbi);
        this.unzipTo(new ZipInputStream(this.getAssets().open("pythonhome." + myAbi + ".zip")), this.getPythonBaseDir());
        this.unzipTo(new ZipInputStream(this.getAssets().open("user-code.zip")), getUserCodeDir());
        this.fixFile(new File(this.getPythonBaseDir() + "/bin/python3"));
        this.fixFile(new File(this.getPythonBaseDir() + "/bin/python3.7"));
    }

    private final void fixFile(File executable) throws Throwable {
        if (executable.exists()) {
            executable.setExecutable(true);
            executable.setReadable(true);
            ProcessBuilder pb = new ProcessBuilder(new String[]{"restorecon", executable.getAbsolutePath()});
            String var3 = "Running restorecon...";
            boolean var4 = false;
            System.out.println(var3);
            Process var7 = pb.start();
            int errCode = var7.waitFor();
            String var5 = "Restorecon finished. result=" + errCode;
            boolean var6 = false;
            System.out.println(var5);
        } else {
            throw (Throwable) (new Error("yikes python is gone wtf"));
        }
    }

    private final String getRubiconBasePath() throws Throwable {
        return this.getPythonBasePath() + "/rubicon_install/";
    }

    private final void unpackRubicon() throws Throwable {
        this.unzipTo(new ZipInputStream(this.getAssets().open("rubicon.zip")), new File(this.getRubiconBasePath()));
    }

    private final void setPythonEnvVars() throws Throwable {
        Os.setenv("RUBICON_LIBRARY", this.getApplicationInfo().nativeLibraryDir + "/librubicon.so", true);
        Log.v("python home", this.getPythonBasePath());
        Context var10001 = this.getApplicationContext();
        Intrinsics.checkExpressionValueIsNotNull(var10001, "applicationContext");
        File var1 = var10001.getCacheDir();
        if (var1 == null) {
            Intrinsics.throwNpe();
        }

        Os.setenv("TMPDIR", var1.getAbsolutePath(), true);
        Os.setenv("LD_LIBRARY_PATH", this.getApplicationInfo().nativeLibraryDir, true);
        Os.setenv("PYTHONHOME", this.getPythonBasePath(), true);
        Os.setenv("ACTIVITY_CLASS_NAME", "org/asheesh/beeware/pythonstubsapp/MainActivity", true);
    }

    private File getUserCodeDir() throws Exception {
        StringBuilder var10002 = new StringBuilder();
        Context var10003 = this.getApplicationContext();
        Intrinsics.checkExpressionValueIsNotNull(var10003, "applicationContext");
        File var3 = var10003.getFilesDir();
        if (var3 == null) {
            Intrinsics.throwNpe();
        }

        File dir = new File(var10002.append(var3.getAbsolutePath()).append("/user-code/").toString());
        if (!dir.exists()) {
            boolean mkdirResult = dir.mkdirs();
            if (!mkdirResult) {
                throw new Exception("Unable to find a place to store the user's code.");
            }
        }

        return dir;

    }

    private final void startPython() throws Throwable {
        this.setPythonEnvVars();
        this.unpackPython();
        this.unpackRubicon();
        int pythonStart = Python.init(this.getPythonBasePath(),
                this.getRubiconBasePath() + ":" + this.getUserCodeDir().getAbsolutePath(),
                (String) null);
        if (pythonStart > 0) {
            throw (Throwable) (new Exception("got an error initializing Python"));
        }
    }

    private final void runPythonString(String s) throws Throwable {
        String fullFilename = this.getPythonBasePath() + "/runme.py";
        FileOutputStream fos = new FileOutputStream(fullFilename);
        Charset var5 = Charsets.UTF_8;
        boolean var6 = false;
        if (s == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
        } else {
            byte[] var10000 = s.getBytes(var5);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).getBytes(charset)");
            byte[] var8 = var10000;
            fos.write(var8);
            fos.close();
            Python.run(fullFilename, new String[0]);
        }
    }

    public static void setPythonApp(IPythonApp app) {
        pythonApp = app;
    }

    private final void extractPythonApp() throws Throwable {
        this.runPythonString("import app.__main__");
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.captureStdoutStderr();
        LinearLayout layout = new LinearLayout((Context) this);
        this.setContentView((View) layout);
        try {
            this.startPython();
            this.extractPythonApp();
        } catch (Throwable e) {
            System.err.println(e);
        }
        pythonApp.onCreate();
    }

    protected void onStart() {
        super.onStart();
        pythonApp.onStart();
    }

    protected void onResume() {
        super.onResume();
        pythonApp.onResume();
    }

    private native boolean captureStdoutStderr();

    static {
        System.loadLibrary("native-lib");
    }
}
