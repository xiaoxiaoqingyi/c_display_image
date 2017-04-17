package com.magicing.ndktest;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.magicing.ndktest.utils.NDKMethod;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;


public class HugeActivity extends AppCompatActivity
{


    private final static String TAG = "MainActivity";
    private SurfaceView surfaceView;
    private Surface mSurface;
    private SurfaceHolder svHolder;
    public final static int CONVERT_FRAME = 111;
    private final static int BASE_RACE = 80;
    private final static int FASTEST_RACE = 20;
    private final static int REQUEST_CODE = 2222;
    private final static int INITIAL_FINISH = 55555;

    private String dirName = "64668954";
    //    private List<String> fileLists = new ArrayList<String>();
    private HashMap<Integer, String> fileLists = new HashMap<Integer, String>();
    private File[] fileArray;
    private boolean isFinished = false;
    /**
     * 标识线程是否在运行
     */
    private  boolean runFlag = true;
    /**
     * 标识线程是否在暂缓
     */
    private boolean suspend = false;
    /**
     * 标识图像转动的方向
     */
    private boolean isRightRotate = false;
    /**
     * 默认显示第一张图片
     */
    private int curFrame = 0;
    /**
     * 间距，每滑动这个间距就换一张图片
     */
    private final static int interval = 30;
    /**
     * 按下时显示的图片
     */
    private int acton_down_page =  0;
    private Object object = new Object();

    float down_x = 0;
    long down_time = 0;
    float up_x = 0;
    long up_time = 0;
    private int curRate = 80;
    private NDKMethod ndk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huge);
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

        initData();
    }



    private void initData(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                getFileList(dirName);
            }
        }.start();
        mHandler.sendEmptyMessage(INITIAL_FINISH);
    }

    private void startRotate(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (runFlag){
                    synchronized (object) {
                        if (suspend) {
                            try {
                                object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if(isRightRotate){ //逆时针
                        if(curFrame <= 0){
                            curFrame = fileLists.size();
                        }
                        --curFrame;
                    }else { //顺时针
                        if(curFrame >= fileLists.size() - 1){
                            curFrame = -1;
                        }
                        ++curFrame;
                    }
                    mHandler.sendEmptyMessage(CONVERT_FRAME);
                    try {
                        Thread.sleep(curRate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void getFileList(String name){
        fileLists.clear();
        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RecordVideo/";// 存放照片的文件夹
        File savedir = new File(savePath + name + "/copy");
        if(!savedir.exists()){
            savedir = new File(savePath + name + "/origin");
            if(!savedir.exists()){
                return;
            }
        }
        fileArray = savedir.listFiles();
        Arrays.sort(fileArray);
        int fileNameLength = 0;
        for (int i=0; i< fileArray.length; i++){
            if(i == 0){
                fileNameLength = fileArray[i].getName().length() - 5;
            }
            int position = 0;
            if(fileArray[i].getName().length() > 8){
                position = Integer.parseInt(fileArray[i].getName().substring(fileNameLength, fileArray[i].getName().length() - 4));
                fileLists.put(position-1, fileArray[i].getAbsolutePath());
            }else {
                position = Integer.parseInt(fileArray[i].getName().substring(0, fileArray[i].getName().length() - 4));
                fileLists.put(position-1, fileArray[i].getAbsolutePath());
            }



        }

    }

    public void setSuspend(boolean suspend) {
        if (!suspend) {
            synchronized (object) {
                object.notifyAll();
            }
        }
        this.suspend = suspend;
    }

    public void leftRotate(){
        this.isRightRotate = false;
    }

    public void rightRotate(){
        this.isRightRotate = true;
    }

    /**
     * 处理滑动时的X坐标改变
     * @param offset_X
     */
    private void slidingHander(float offset_X){
        if(!isFinished)
            return;
        if((offset_X- down_x)/interval > 1){
            curFrame = acton_down_page - (int)(offset_X- down_x)/interval;
            turnRight();

        }else if((offset_X - down_x )/interval < -1){
            curFrame = acton_down_page + Math.abs((int)(offset_X- down_x)/interval);
            turnLeft();
        }
    }


    private void turnRight(){
        if(curFrame < 0 ){
            curFrame = curFrame % fileLists.size() + fileLists.size();
        }

        ndk.showJPG(mSurface, fileLists.get(curFrame));
    }

    private void turnLeft(){
        if(curFrame > fileLists.size() - 1){
            curFrame = curFrame % fileLists.size();
        }
        ndk.showJPG(mSurface, fileLists.get(curFrame));
    }

    /**
     * 处理用户离开屏幕动作
     * @param offset_X
     */
    private void actionUpHander(float offset_X){
        if(!isFinished)
            return;
        acton_down_page = curFrame;
        up_x = offset_X;
        up_time = System.currentTimeMillis();
        float rate = (up_x - down_x)/(up_time - down_time);

        if(rate > 0){
            rightRotate();
        }else {
            leftRotate();
        }

        if(rate < 0.8 && rate > -0.8){
            setSuspend(true);
        }else if(rate > 3 || rate < -3){
            curRate = FASTEST_RACE;
            if(suspend){
                setSuspend(false);
            }
        }else if(rate >= 0.8 && rate <= 3){
            curRate = (int) (BASE_RACE/rate);
            if(suspend){
                setSuspend(false);
            }
        }else if(rate >= -3 && rate <= -0.8){
            curRate = (int) Math.abs(BASE_RACE/rate);
            if(suspend){
                setSuspend(false);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setSuspend(true);
                down_time = System.currentTimeMillis();
                down_x = event.getX();
                acton_down_page = curFrame;
                break;

            case MotionEvent.ACTION_MOVE:
                slidingHander(event.getX());
                break;

            case MotionEvent.ACTION_UP:
                actionUpHander(event.getX());
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        runFlag = false;
        isFinished = false;
        System.gc();
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CONVERT_FRAME:
//                    Log.i("ACTION","curFrame="+curFrame);
                    ndk.showJPG(mSurface, fileLists.get(curFrame));

                    break;
                case  INITIAL_FINISH:
                    isFinished = true;
                    startRotate();
                    break;
            }
        }
    };

}
