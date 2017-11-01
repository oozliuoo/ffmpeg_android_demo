//
// Created by Zhexuan Liu on 10/31/17.
//

#include "demo_scaling.h"
#include <jni.h>

JNIEXPORT jstring Java_com_example_zhexuanliu_swsscaledemo_MainActivity_stringFromNative(
    JNIEnv *env,
    jobject callingObject)
    {
        return (*env) -> NewStringUTF(env, "Native code rules!");
    }