package com.evan.img2mp4

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Log
import android.util.Size
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

fun createFloatBuffer(array: FloatArray): FloatBuffer {
    val buffer = ByteBuffer
        // 分配顶点坐标分量个数 * Float占的Byte位数
        .allocateDirect(array.size * 4)
        // 按照本地字节序排序
        .order(ByteOrder.nativeOrder())
        // Byte类型转Float类型
        .asFloatBuffer()

    // 将Dalvik的内存数据复制到Native内存中
    buffer.put(array).position(0)
    return buffer
}

/**
 *
 * @param needEnd when bufferId is INFO_TRY_AGAIN_LATER, is need to break loop
 * */
fun MediaCodec.handleOutputBuffer(
    bufferInfo: MediaCodec.BufferInfo, defTimeOut: Long,
    formatChanged: () -> Unit = {},
    render: (bufferId: Int) -> Unit,
    needEnd: Boolean = true
) {
    loopOut@ while (true) {
        //  获取可用的输出缓存队列
        val outputBufferId = dequeueOutputBuffer(bufferInfo, defTimeOut)
        Log.d("handleOutputBuffer", "output buffer id : $outputBufferId ")
        if (outputBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
            if (needEnd) {
                break@loopOut
            }
        } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            formatChanged.invoke()
        } else if (outputBufferId >= 0) {
            render.invoke(outputBufferId)
            if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                break@loopOut
            }
        }
    }
}

fun createVideoFormat(
    size: Size, colorFormat: Int = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,
    bitRate: Int, frameRate: Int, iFrameInterval: Int
): MediaFormat {
    return MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, size.width, size.height)
        .apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat)

            // 大部分机型无效
//                setInteger(MediaFormat.KEY_PROFILE, MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline)
//                setInteger(MediaFormat.KEY_LEVEL, MediaCodecInfo.CodecProfileLevel.AVCLevel11)
//                setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ)

            setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
            setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval)
        }
}

/**
 * 每一帧时间，微秒
 * */
val MediaFormat.perFrameTime: Long
    get() {
        return 1000000L / this.fps
    }

// 每秒传输帧数
val MediaFormat.fps: Int
    get() = try {
        getInteger(MediaFormat.KEY_FRAME_RATE)
    } catch (e: Exception) {
        0
    }
