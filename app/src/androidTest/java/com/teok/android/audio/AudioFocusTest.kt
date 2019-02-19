package com.teok.android.audio

import android.content.Context
import android.media.AsyncPlayer
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioFocusTest {

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
    fun test_audioFocus_request_api21_pre() {
        val listener = AudioManager.OnAudioFocusChangeListener {
            Log.d(TAG, "audio focus changed: $it")
        }

        fun requestAudioFocusApi21Pre(streamType: Int, hint: Int): Int {
            return mAudioManager.requestAudioFocus(listener, streamType, hint)
        }

        val result = requestAudioFocusApi21Pre(AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        assertEquals(AudioManager.AUDIOFOCUS_GAIN, result)
        Thread.sleep(1000L)

        val result2 = requestAudioFocusApi21Pre(AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        assertEquals(AudioManager.AUDIOFOCUS_GAIN, result2)
        Thread.sleep(1000L)
    }

    @Test
    fun test_audioFocus_request_api21_pre_with_playingMusic() {
        val listener = AudioManager.OnAudioFocusChangeListener {
            Log.d(TAG, "audio focus changed: $it")
        }

        fun requestAudioFocusApi21Pre(streamType: Int, hint: Int): Int {
            return mAudioManager.requestAudioFocus(listener, streamType, hint)
        }

        val result = requestAudioFocusApi21Pre(AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        assertEquals(AudioManager.AUDIOFOCUS_GAIN, result)
        playMusic()
        Thread.sleep(1000L)

        val result2 = requestAudioFocusApi21Pre(AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        assertEquals(AudioManager.AUDIOFOCUS_GAIN, result2)
        Thread.sleep(10000L)
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
        const val TAG = "AudioFocusTest"
        const val PLAYER_TAG = "AudioFocusTestPlayer"
    }
}