package org.asheesh.beeware.pythonstubsapp

import android.os.Build
import android.os.Bundle
import android.system.Os
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import org.beeware.rubicon.Python
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

class MainActivity : AppCompatActivity() {
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    private val pythonBasePath: String
        get() {
            return pythonBaseDir.absolutePath
        }

    private val pythonBaseDir: File
        get() {
            val dir = File(applicationContext.filesDir!!.absolutePath + "/python/")
            if (!dir.exists()) {
                val mkdirResult = dir.mkdirs()
                if (!mkdirResult) {
                    throw Exception("Unable to find a place to store the Python stdlib.")
                }
            }
            return dir
        }

    private fun unzipTo(inputStream: ZipInputStream, outputDir: File) {
        if (outputDir.exists()) {
            Log.d("unpackPython", "deleting recursively")
            outputDir.deleteRecursively()
            Log.d("unpackPython", "deleting recursively done")
        }
        if (!outputDir.mkdirs()) {
            throw RuntimeException("Unable to mkdir ${outputDir.absolutePath}")
        }
        var zipEntry = inputStream.nextEntry
        val buf = ByteArray(1024 * 1024 * 4)
        while (zipEntry != null) {
            val outputFile = File("${outputDir.absolutePath}/${zipEntry}")
            if (zipEntry.isDirectory) {
                Log.d("unzipTo", "creating dir ${outputFile.absolutePath}")
                val result = outputFile.mkdirs()
                if (!result) {
                    Log.d("unzipTo", "mkdirs result = $result")
                }
                zipEntry = inputStream.nextEntry
                continue
            }
            Log.d("unzipTo", "about to create file ${outputFile.absolutePath}")
            val fos = FileOutputStream(outputFile.absolutePath)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                fos.write(buf, 0, len)
            }
            fos.close()
            zipEntry = inputStream.nextEntry
        }
        inputStream.closeEntry()
        inputStream.close()
    }

    private fun unpackPython() {
        val myAbi = Build.SUPPORTED_ABIS[0];
        Log.e("unpackPython", "abi is ${myAbi}")
        unzipTo(ZipInputStream(assets.open("pythonhome.${myAbi}.zip")), pythonBaseDir)
        fixFile(File("${pythonBaseDir}/bin/python3"))
        fixFile(File("${pythonBaseDir}/bin/python3.7"))
    }

    private fun fixFile(executable: File) {
        if (executable.exists()) {
            executable.setExecutable(true)
            executable.setReadable(true)
        } else {
            throw Error("yikes python is gone wtf")
        }
        // See if magical restorecon saves the day
        val pb =
                ProcessBuilder("restorecon", executable.absolutePath)
        println("Running restorecon...")
        val process = pb.start()
        val errCode = process.waitFor()
        println("Restorecon finished. result=${errCode}")
    }


    private val rubiconBasePath: String
        get() {
            return "${pythonBasePath}/rubicon_install/"
        }

    private fun unpackRubicon() {
        unzipTo(ZipInputStream(assets.open("rubicon.zip")), File(rubiconBasePath))
    }

    private fun setPythonEnvVars() {
        Os.setenv("RUBICON_LIBRARY", "${applicationInfo.nativeLibraryDir}/librubicon.so", true)
        Log.v(
                "python home",
                pythonBasePath
        )
        Os.setenv("TMPDIR", applicationContext.cacheDir!!.absolutePath, true)
        // Android needs LD_LIBRARY_PATH set in order for subprocesses to be able to use
        // libraries from the app.
        Os.setenv("LD_LIBRARY_PATH", applicationInfo.nativeLibraryDir, true)
        // Any Python subprocesses are going to need PYTHONHOME configured.
        Os.setenv("PYTHONHOME", pythonBasePath, true)
    }

    private fun startPython() {
        setPythonEnvVars()
        unpackPython()
        unpackRubicon()
        val pythonStart = Python.init(pythonBasePath, rubiconBasePath, null)
        if (pythonStart > 0) {
            throw Exception("got an error initializing Python")
        }
    }
    private fun runPythonString(s: String) {
        val fullFilename = "${pythonBasePath}/runme.py"
        val fos = FileOutputStream(fullFilename)
        fos.write(s.toByteArray())
        fos.close()
        Python.run(fullFilename, arrayOf())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        captureStdoutStderr()
        val layout = LinearLayout(this)
        setContentView(layout)
        startPython()
        runPythonString("print('hi')")
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private external fun captureStdoutStderr(): Boolean
}