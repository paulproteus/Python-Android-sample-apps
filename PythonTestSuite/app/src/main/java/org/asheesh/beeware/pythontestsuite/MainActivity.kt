package org.asheesh.beeware.pythontestsuite

import android.os.Bundle
import android.system.Os
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.beeware.rubicon.Python
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream


class MainActivity : AppCompatActivity() {

    private fun unpackPython() {
        val destDir = applicationContext.dataDir!!
        val existingLib = File(destDir.absolutePath + "/lib/")
        if (existingLib.exists()) {
            Log.d("unpackPython", "deleting recursively")
            existingLib.deleteRecursively()
            Log.d("unpackPython", "deleting recursively done")
        }
        val zis = ZipInputStream(assets.open("pythonhome.zip"))
        var zipEntry = zis.nextEntry
        val buf = ByteArray(1024 * 1024 * 4)
        while (zipEntry != null) {
            val outputFile = File(destDir.absolutePath + "/" + zipEntry)
            if (zipEntry.isDirectory) {
                outputFile.mkdirs()
                zipEntry = zis.nextEntry
                continue
            }
            val fos = FileOutputStream(outputFile)
            var len: Int
            while (zis.read(buf).also { len = it } > 0) {
                fos.write(buf, 0, len)
            }
            fos.close()
            zipEntry = zis.nextEntry
        }
        zis.closeEntry()
        zis.close()
        val python = File(applicationContext.dataDir!!.absolutePath + "/bin/python3")
        if (python.exists()) {
            python.setExecutable(true)
            python.setReadable(true)
        } else {
            throw Error("yikes python is gone wtf")
        }
    }

    private fun unpackRubicon() {
        // TODO: Avoid unpacking if already unpacked.
        val destDir = applicationContext.dataDir.absolutePath + "/rubicon-hmm/"
        val zis = ZipInputStream(assets.open("rubicon.zip"))
        var zipEntry = zis.nextEntry
        val buf = ByteArray(1024 * 1024 * 4)
        while (zipEntry != null) {
            val outputFile = File(destDir + zipEntry)
            if (zipEntry.isDirectory) {
                outputFile.mkdirs()
                zipEntry = zis.nextEntry
                continue
            }
            val fos = FileOutputStream(outputFile)
            var len: Int
            while (zis.read(buf).also { len = it } > 0) {
                fos.write(buf, 0, len)
            }
            fos.close()
            zipEntry = zis.nextEntry
        }
        zis.closeEntry()
        zis.close()
    }

    private fun setPythonEnvVars() {
        // Unpack Python into cache directory -- use applicationContext.externalCacheDir
        Os.setenv(
            "PYTHONHOME",
            applicationContext.dataDir!!.absolutePath,
            true
        )
        Os.setenv("PYTHONPATH", applicationContext.dataDir.absolutePath + "/rubicon-hmm/", true)
        Os.setenv("RUBICON_LIBRARY", applicationInfo.nativeLibraryDir + "/librubicon.so", true)
        Log.v(
            "python home",
            applicationContext.dataDir!!.absolutePath
        )
        Os.setenv("TMPDIR", applicationContext.cacheDir!!.absolutePath, true)
    }

    private fun startPython() {
        val pythonStart = Python.init(null, ".", null)
        if (pythonStart > 0) {
            throw Exception("got an error initializing Python")
        }
    }

    private fun runPythonString(s: String) {
        val fullFilename = applicationContext.dataDir!!.absolutePath + "/runme.py"
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
        setPythonEnvVars()
        startPython()
        unpackRubicon()
        runPythonString(
            // bring test_logging back soon
            """
            import sys
            import os
            import random
            l = []
            random.shuffle(l)
            sys.executable = os.environ.get("PYTHONHOME") + "/bin/python3"
            from test.libregrtest import main
            import ctypes
            import sys
            main(l)
        """.trimIndent()
        )

        sample_text.text = "Hello from Kotlin!"
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
