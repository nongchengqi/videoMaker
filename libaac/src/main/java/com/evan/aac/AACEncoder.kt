package com.evan.aac

object AACEncoder {
    init {
        System.loadLibrary("aacenc")
    }

    external fun encodePcmToAAC(
        inPath: String,
        outPath: String,
        bit: Int,
        sampleRate: Int,
        channel: Int
    )
}