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

JNIEXPORT jbyteArray Java_com_example_zhexuanliu_swsscaledemo_MainActivity_scaileImage(
        JNIEnv *env,
        jobject  obj,
        jbyteArray source,
        int src_width,
        int src_height,
        int dst_width,
        int dst_height,
        int frame_num
    )
{
    enum AVPixelFormat pix_fmt = AV_PIX_FMT_YUVJ420P;
    // size for yuv buffer
    const int read_frame_size = src_width * src_height * 3 / 2;
    const int write_frame_size = dst_width * dst_height * 3 / 2;
    const int read_size = read_frame_size * frame_num;
    const int write_size = write_frame_size * frame_num;

    struct SwsContext *img_convert_ctx;
    uint8_t *inbuf[4];
    uint8_t *outbuf[4];

    // setup variables
    int inlinesize[4] = {src_width, src_width/2, src_width/2, 0};
    int outlinesize[4] = {dst_width, dst_width/2, dst_width/2, 0};

    uint8_t *temp;
    uint8_t in[read_size];
    uint8_t out[write_size];

    temp = (*env) -> GetByteArrayElements(env, source, NULL);

    for (int i = 0; i < read_size; i ++)
    {
        in[i] = *(temp + i);
    }

    inbuf[0] = malloc(src_width * src_height);
    inbuf[1] = malloc(src_width * src_height >> 2);
    inbuf[2] = malloc(src_width * src_height >> 2);
    inbuf[3] = NULL;

    outbuf[0] = malloc(dst_width * dst_height);
    outbuf[1] = malloc(dst_width * dst_height >> 2);
    outbuf[2] = malloc(dst_width * dst_height >> 2);
    outbuf[3] = NULL;

    // Initialize convert context
    img_convert_ctx = sws_getContext(
            src_width,
            src_height,
            pix_fmt,
            dst_width,
            dst_height,
            pix_fmt,
            SWS_POINT,
            NULL, NULL, NULL
    );

    if(img_convert_ctx == NULL) {
        fprintf(stderr, "Cannot initialize the conversion context!\n");
        return NULL;
    }

    int i = 0;

    for (i = 0; i < frame_num; i ++)
    {
        long offset = i * src_width * src_height * 3 / 2;
        // printf("input offset: %ld\n", offset);
        memcpy(inbuf[0], in + offset, src_width * src_height);
        memcpy(inbuf[1], in + offset + src_width * src_height, src_width * src_height >> 2);
        memcpy(inbuf[2], in + offset + (src_width * src_height * 5 >> 2), src_width * src_height >> 2);

        // start sws_scale
        sws_scale(
                img_convert_ctx,
                (const uint8_t * const*) inbuf,
                inlinesize,
                0,
                src_height,
                outbuf,
                outlinesize
        );

        long output_offset = i * dst_width * dst_height * 3 / 2;
        // printf("output offset: %ld\n", output_offset);
        memcpy(out + output_offset, outbuf[0], dst_width * dst_height);
        memcpy(out + output_offset + dst_width * dst_height, outbuf[1], dst_width * dst_height >> 2);
        memcpy(out + output_offset + (dst_width * dst_height * 5 >> 2), outbuf[2], dst_width * dst_height >> 2);
    }

    // release the ConvertContext
    sws_freeContext(img_convert_ctx);

    // setup and return the byte array to java
    jbyteArray  result = (*env) -> NewByteArray(env, write_size);

    (*env) -> SetByteArrayRegion(env, result, 0, write_size, (jbyte*) out);
    return result;
}