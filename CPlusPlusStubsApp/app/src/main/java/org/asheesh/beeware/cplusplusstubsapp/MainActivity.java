package org.asheesh.beeware.cplusplusstubsapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

interface Lambda {
    void run();
}

public class MainActivity extends AppCompatActivity {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nativeLog("onCreate called");
        LinearLayout layout = new LinearLayout(this);
        setContentView(layout);
        Button button = createButton();
        layout.addView(button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        nativeLog("onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        nativeLog("onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        nativeLog("onPause called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nativeLog("onDestroy called");
    }

    private native Button createButton();

    private native void nativeLog(String msg);
}
