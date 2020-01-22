package org.asheesh.beeware.pythontestsuite

import android.os.Build
import android.os.Bundle
import android.system.Os
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.beeware.rubicon.Python
import java.io.File
import java.io.FileOutputStream
import java.lang.RuntimeException
import java.util.zip.ZipInputStream


class MainActivity : AppCompatActivity() {
    private val pythonBasePath: String
    get()  {
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
        val python = File("${pythonBaseDir}/bin/python3")
        if (python.exists()) {
            python.setExecutable(true)
            python.setReadable(true)
        } else {
            throw Error("yikes python is gone wtf")
        }
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
    }

    private fun startPython() {
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
        setContentView(R.layout.activity_main)

        // Capture stdout/stderr so we can read output from Python.
        captureStdoutStderr()

        unpackPython()
        unpackRubicon()
        setPythonEnvVars()
        startPython()
        runPythonString(
            """
            import sys
            sys.executable = sys.prefix + "/bin/python3"
            from test.libregrtest import main
            try:
                main([])
            except SystemExit as e:
                # Do not let SystemExit bubble up further; if the app exits with a nonzero
                # status code, Android restarts it, which is rather annoying. :)
                print('Would exit with statuscode', e.code)
        """.trimIndent()
        )
        sample_text.text = "Hello from Kotlin!"
        finishAndRemoveTask()
    }

    /**
     * A native method to capture stdio/stderr and copy them to the Android log.
     */
    external fun captureStdoutStderr(): Boolean

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
