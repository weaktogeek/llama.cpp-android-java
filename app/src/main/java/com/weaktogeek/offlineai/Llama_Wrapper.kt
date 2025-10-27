package com.weaktogeek.offlineai

import android.llama.cpp.LLamaAndroid
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Llama_Wrapper(private val llamaAndroid: LLamaAndroid = LLamaAndroid.instance()) {
    interface MessageHandler {
        fun h(msg: String)
    }

    fun load(path: String) {
        GlobalScope.launch {
            llamaAndroid.load(path)
        }
    }

    fun unload() {
        GlobalScope.launch {
            llamaAndroid.unload()
        }
    }

    fun send(msg: String, mh: MessageHandler) {
        GlobalScope.launch {
            llamaAndroid.send(msg).collect { mh.h(it) }
        }
    }

}