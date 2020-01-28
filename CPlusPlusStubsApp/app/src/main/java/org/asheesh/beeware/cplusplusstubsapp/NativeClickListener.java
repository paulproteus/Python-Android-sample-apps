package org.asheesh.beeware.cplusplusstubsapp;

import android.view.View;
import android.widget.Button;

public class NativeClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        callOnClick((Button) v);
    }


    private native void callOnClick(Button button);

}

