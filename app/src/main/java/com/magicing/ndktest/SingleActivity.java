package com.magicing.ndktest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.magicing.ndktest.utils.NDKMethod;

public class SingleActivity extends AppCompatActivity {

    private static final String TAG = "SingleActivity";
    private NDKMethod ndk;
    private SurfaceView surfaceView;
    private Surface mSurface;
    private SurfaceHolder svHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        init();
    }

    private void init(){
        ndk = new NDKMethod();
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        svHolder = surfaceView.getHolder();
        svHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG, "surfaceCreated=");
                mSurface = holder.getSurface();

                String path = "/storage/emulated/0/RecordVideo/64668954/origin/11.jpg";
                ndk.showJPG(mSurface, path);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG, "surfaceChanged format=" + format + ", width="
                        + width + ", height=" + height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "surfaceDestroyed");
            }
        });

    }
}
