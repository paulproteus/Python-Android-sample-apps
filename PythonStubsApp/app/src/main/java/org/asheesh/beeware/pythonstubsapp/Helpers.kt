package org.asheesh.beeware.pythonstubsapp

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOError
import java.io.IOException
import java.util.zip.ZipInputStream

const val TAG = "Helpers"

fun makeExecutable(executable: File) {
    if (executable.exists()) {
        executable.setExecutable(true)
        executable.setReadable(true)
    } else {
        throw IOException("Executable file is missing. Aborting.")
    }
    // See if magical restorecon saves the day
    val pb =
            ProcessBuilder("restorecon", executable.absolutePath)
    println("Running restorecon...")
    val process = pb.start()
    val errCode = process.waitFor()
    println("Restorecon finished. result=${errCode}")
}

fun unzipTo(inputStream: ZipInputStream, outputDir: File) {
    if (outputDir.exists()) {
        Log.d(TAG, "deleting recursively")
        outputDir.deleteRecursively()
        Log.d(TAG, "deleting recursively done")
    }
    if (!outputDir.mkdirs()) {
        throw RuntimeException("Unable to mkdir ${outputDir.absolutePath}")
    }
    var zipEntry = inputStream.nextEntry
    val buf = ByteArray(1024 * 1024 * 4)
    while (zipEntry != null) {
        val outputFile = File("${outputDir.absolutePath}/${zipEntry}")
        if (zipEntry.isDirectory) {
            Log.d(TAG, "creating dir ${outputFile.absolutePath}")
            val result = outputFile.mkdirs()
            if (!result) {
                Log.d("unzipTo", "mkdirs result = $result")
            }
            zipEntry = inputStream.nextEntry
            continue
        }
        Log.d(TAG, "about to create file ${outputFile.absolutePath}")
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
