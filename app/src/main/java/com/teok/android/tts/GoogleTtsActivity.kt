package com.teok.android.tts

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import com.teok.android.common.ListedButtonActivity
import com.teok.android.common.showShortToast
import java.util.*

class GoogleTtsActivity : ListedButtonActivity() {

    private lateinit var mTts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mTts = TextToSpeech(this, TextToSpeech.OnInitListener {
            showShortToast(this@GoogleTtsActivity, "TTS Init: $it")
        })

        addButton("Play Locale.US", View.OnClickListener {
            mTts.language = Locale.US
            val uid = "someId"
            mTts.speak("Hello the wonderful world!", TextToSpeech.QUEUE_FLUSH, null, uid)
        })

        addButton("Play Locale.Chinese", View.OnClickListener {
            mTts.language = Locale.SIMPLIFIED_CHINESE
            val uid = "someChineseId"
            mTts.speak("你好啊，美妙的世界!", TextToSpeech.QUEUE_FLUSH, null, uid)
        })
    }

}