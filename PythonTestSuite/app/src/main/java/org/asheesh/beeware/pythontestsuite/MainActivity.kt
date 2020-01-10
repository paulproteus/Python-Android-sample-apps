package org.asheesh.beeware.pythontestsuite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Capture stdout/stderr so we can read output from Python.
        captureStdoutStderr()

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
