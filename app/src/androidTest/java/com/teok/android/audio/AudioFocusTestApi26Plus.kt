package com.teok.android.audio

import android.content.Context
import android.media.AsyncPlayer
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class AudioFocusTestApi26Plus {

    private lateinit var mAudioManager: AudioManager
    private lateinit var mContext: Context
    private lateinit var mPlayer: AsyncPlayer

    @Before
    fun setup() {
        mContext = InstrumentationRegistry.getInstrumentation().targetContext
        mAudioManager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mPlayer = AsyncPlayer(PLAYER_TAG)

        assertNotNull(mContext)
        assertNotNull(mAudioManager)
    }

    @After
    fun tearDown() {
        mPlayer.stop()
    }

    @Test
    fun request_focus() {
        fun createHandler(threadName: String): Handler {
            val handlerThread = HandlerThread(threadName)
            handlerThread.start()
            return Handler(handlerThread.looper)
        }
        val afChangeListenerNavi = mock(AudioManager.OnAudioFocusChangeListener::class.java)
        val handlerNavi = createHandler("handlerThreadNavi")

        val focusRequestNavi = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                build()
            })
            setAcceptsDelayedFocusGain(false)
            setOnAudioFocusChangeListener(afChangeListenerNavi, handlerNavi)
            build()
        }
        val resultNavi = mAudioManager.requestAudioFocus(focusRequestNavi)
        assertEquals(AudioManager.AUDIOFOCUS_REQUEST_GRANTED, resultNavi)

        val afChangeListenerMusic = mock(AudioManager.OnAudioFocusChangeListener::class.java)
        val handlerMusic = createHandler("handlerThreadMusic")
        val focusRequestMusic = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_MEDIA)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(afChangeListenerMusic, handlerMusic)
            build()
        }
        val resultMusic = mAudioManager.requestAudioFocus(focusRequestMusic)
        assertEquals(AudioManager.AUDIOFOCUS_REQUEST_GRANTED, resultMusic)
    }

    private fun playMusic() {
        mPlayer.play(mContext,
                Uri.parse("android.resource://" + mContext.packageName + "/raw/sample_audio_0_7mb"),
                true,
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
    }

    companion object {
        const val TAG = "AudioFocusTestPreApi21"
        const val PLAYER_TAG = "AudioFocusTestPlayer"
    }
}