#include <jni.h>
#include "android/log.h"
#include <string>

extern "C" JNIEXPORT jobject JNICALL
Java_org_asheesh_beeware_cplusplusstubsapp_MainActivity_createButton(
        JNIEnv *env,
        jobject thisObj) {
    jclass buttonClass = env->FindClass("android/widget/Button");
    jmethodID buttonConstructor = env->GetMethodID(buttonClass, "<init>",
                                                   "(Landroid/content/Context;)V");
    jobject button = env->NewObject(buttonClass, buttonConstructor, thisObj);
    return button;
//    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_org_asheesh_beeware_cplusplusstubsapp_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_org_asheesh_beeware_cplusplusstubsapp_MainActivity_nativeLog(JNIEnv *env,
                                                                  jobject /* this */,
                                                                  jstring message) {
    const char *messageUTF = env->GetStringUTFChars(message, NULL);
    __android_log_write(ANDROID_LOG_DEBUG, "cplusplusstubsapp.nativeLog", messageUTF);
}
