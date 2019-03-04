package com.teok.android.audio

import android.content.Context
import android.media.AsyncPlayer
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AudioFocusTestApi21To26 {

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