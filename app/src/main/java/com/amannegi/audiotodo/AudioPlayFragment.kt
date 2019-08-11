package com.amannegi.audiotodo

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AudioPlayFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_audio_play, container, false)

        val bundle = arguments
        val fileName = bundle?.getString(MainActivity.KEY_NAME)
        val filePath = bundle?.getString(MainActivity.KEY_PATH)

        val txtAudioName: TextView = view.findViewById(R.id.txt_audio_name)
        val fabStopPlaying: FloatingActionButton = view.findViewById(R.id.fab_stop_playing)
        val playingAnimImageView: ImageView = view.findViewById(R.id.playing_anim_image_view)

        val animated = AnimatedVectorDrawableCompat.create(context!!, R.drawable.anim_play)
        animated?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                playingAnimImageView.post { animated.start() }
            }
        })
        playingAnimImageView.setImageDrawable(animated)
        animated?.start()

        txtAudioName.text = fileName

        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(filePath)
        mediaPlayer.prepare()
        mediaPlayer.start()

        fabStopPlaying.setOnClickListener {
            mediaPlayer.stop()
            mediaPlayer.release()
            dismiss()
        }

        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
            dismiss()
        }

        return view
    }

}
