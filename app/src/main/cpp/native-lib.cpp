#include <jni.h>
#include <string>
#include "HVite.h"

#include <android/log.h>

#define APPNAME "lichard49"

extern "C"

JNIEXPORT int Java_com_lichard49_myapplication_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */,
        jstring testFramePathString) {
    int result;
    char *testFramePath = strdup(env->GetStringUTFChars(testFramePathString, 0));

    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Hiiiiii", 1);
    char *args[] =
    {
        "HVite",
         "-A",
        "-T", "1",
        "-H", "/storage/emulated/legacy/hmm_data/newMacros",
        "-i", "/storage/emulated/legacy/hmm_data/recognition_result",
        "-w", "/storage/emulated/legacy/hmm_data/word.lattice",
        "-n", "10", "20",
        "/storage/emulated/legacy/hmm_data/dict",
        "/storage/emulated/legacy/hmm_data/commands",
        testFramePath
    };

    result = gogogo(16, args);
    env->ReleaseStringUTFChars(testFramePathString, testFramePath);
    return result;
    //return 51;
}