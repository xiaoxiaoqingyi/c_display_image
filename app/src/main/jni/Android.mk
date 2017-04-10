LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := jpegbither
LOCAL_SRC_FILES := libjpegbither.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := imagetool
LOCAL_SRC_FILES := com_magicing_ndktest_MainActivity.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_SHARED_LIBRARIES := jpegbither
LOCAL_LDLIBS += -landroid -llog -ljnigraphics
include $(BUILD_SHARED_LIBRARY)
