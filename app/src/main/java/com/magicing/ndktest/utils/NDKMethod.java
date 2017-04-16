package com.magicing.ndktest.utils;

import android.view.Surface;

/**
 * Created by Administrator on 2016/8/9.
 */
public class NDKMethod{

    static {
        System.loadLibrary("imagetool");//导入生成的链接库文件
    }

    public native void showJPG(Surface surface, String img);

    public native void showImage(Surface surface, String path);
}
