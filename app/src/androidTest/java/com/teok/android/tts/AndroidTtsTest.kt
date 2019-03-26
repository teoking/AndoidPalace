package com.teok.android.tts

import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.*

@RunWith(AndroidJUnit4::class)
class AndroidTtsTest {

    private lateinit var tts: TextToSpeech

    @Before
    fun setup() {
        Timber.plant(Timber.DebugTree())

        runBlocking {
            tts = TextToSpeech(
                    InstrumentationRegistry.getInstrumentation().targetContext,
                    OnInitListener {
                        assertEquals(TextToSpeech.SUCCESS, it)
                    }
            )
            delay(1000)
        }
    }

    @After
    fun tearDown() {
        tts.shutdown()
    }

    @Test
    fun availableEngines() {
        val engines = tts.engines
        assertTrue(engines.size > 0)

        Timber.tag(TAG).d("Available engines: $engines")
    }

    @Test
    fun speakWithLangUS() {
        val utteranceId = "us-abc"

        val langAvailable = tts.isLanguageAvailable(Locale.US)
        assertResultValid(langAvailable)

        val langValid = tts.setLanguage(Locale.US)
        assertResultValid(langValid)

        val ret = tts.speak("Hello the big world!", TextToSpeech.QUEUE_ADD, null, utteranceId)
        assertEquals(TextToSpeech.SUCCESS, ret)

        Thread.sleep(5000)
    }

    @Test
    fun speakWithLangChinese() {
        val utteranceId = "abc"

        val langAvailable = tts.isLanguageAvailable(Locale.CHINESE)
        assertResultValid(langAvailable)

        val langValid = tts.setLanguage(Locale.CHINESE)
        assertResultValid(langValid)

        val ret = tts.speak("张三李四", TextToSpeech.QUEUE_ADD, null, utteranceId)
        assertEquals(TextToSpeech.SUCCESS, ret)

        Thread.sleep(5000)
    }

    private fun assertResultValid(ret: Int) {
        assertNotEquals(LANG_MISSING_DATA, ret)
        assertNotEquals(LANG_NOT_SUPPORTED, ret)
    }

    companion object {
        const val TAG = "AndroidTtsTest"
    }

}