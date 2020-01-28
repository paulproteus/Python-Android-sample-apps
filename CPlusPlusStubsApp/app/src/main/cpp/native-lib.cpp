#include <jni.h>
#include "android/log.h"
#include <string>

extern "C" JNIEXPORT jobject JNICALL
Java_org_asheesh_beeware_cplusplusstubsapp_MainActivity_createButton(
        JNIEnv *env,
        jobject thisObj) {
    // button = new Button();
    jclass buttonClass = env->FindClass("android/widget/Button");
    jmethodID buttonConstructor = env->GetMethodID(buttonClass, "<init>",
                                                   "(Landroid/content/Context;)V");
    jobject button = env->NewObject(buttonClass, buttonConstructor, thisObj);

    // button.setText("Hello from C++!");
    jmethodID setText = env->GetMethodID(buttonClass, "setText",
                                         "(Ljava/lang/CharSequence;)V");
    jstring hello = env->NewStringUTF("Hello from C++!");
    env->CallVoidMethod(button, setText, hello);

    // nativeClickListener = new NativeClickListener();
    jclass onClickListenerClass = env->FindClass(
            "org/asheesh/beeware/cplusplusstubsapp/NativeClickListener");
    jmethodID nativeClickListenerConstructor = env->GetMethodID(onClickListenerClass, "<init>",
                                                                "()V");
    jobject nativeClickListener = env->NewObject(onClickListenerClass,
                                                 nativeClickListenerConstructor);

    // button.setOnClickListener(onClickListener);
    jmethodID setOnClickListener = env->GetMethodID(buttonClass, "setOnClickListener",
                                                    "(Landroid/view/View$OnClickListener;)V");
    env->CallVoidMethod(button, setOnClickListener, nativeClickListener);
    return button;
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

extern "C" JNIEXPORT void JNICALL
Java_org_asheesh_beeware_cplusplusstubsapp_NativeClickListener_callOnClick(JNIEnv *env,
                                                                           jobject /* this */,
                                                                           jobject button) {
    __android_log_write(ANDROID_LOG_DEBUG, "cplusplusstubsapp.nativeLog",
                        "About to call native onclick()");
    jclass buttonClass = env->FindClass("android/widget/Button");
    jmethodID setText = env->GetMethodID(buttonClass, "setText",
                                         "(Ljava/lang/CharSequence;)V");
    jstring msg = env->NewStringUTF("Hello from C++ click handler!");
    env->CallVoidMethod(button, setText, msg);
}
