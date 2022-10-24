package com.evan.aac

object Lame {
    init {
        System.loadLibrary("lame")
    }

    fun execute(cmd: String): Int {
        return executeNative(cmd.split(" ").toTypedArray())
    }

    private external fun executeNative(cmd: Array<String>): Int


}