JNIPATH := $(call my-dir)
LOCAL_PATH := $(JNIPATH)

include $(call all-subdir-makefiles)

LOCAL_PATH := $(JNIPATH)
include $(CLEAR_VARS)

LOCAL_MODULE    := myjni
LOCAL_SRC_FILES := hello-jni.c
LOCAL_SRC_FOLDERS := HLMLib HLMTools HTK HTKLib HTKLVRec HTKTools
LOCAL_STATIC_LIBRARIES := htk

include $(BUILD_SHARED_LIBRARY)
