package org.asheesh.beeware.cplusplusstubsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

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
        createButton();
        Button button = createButton();
        // TODO: Move the next two expressions into C++.
        button.setText("Button created from Java & C++ together");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) v).setText("Renamed the button from Java");
            }
        });
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
