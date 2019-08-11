package com.amannegi.audiotodo

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class RecordingFragment : DialogFragment() {
    private val recorder = MediaRecorder()
    private var recorderPaused = false

    companion object {
        val FOLDER_PATH = Environment.getExternalStorageDirectory().toString() + "/AudioToDo Recordings"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recording, container, false)
        val fabPause: FloatingActionButton = view.findViewById(R.id.fab_pause)
        val fabStop: FloatingActionButton = view.findViewById(R.id.fab_stop)
        val fabSave: FloatingActionButton = view.findViewById(R.id.fab_save)
        val edtAudioName: EditText = view.findViewById(R.id.edt_audio_name)
        val recordingAnimImageView: ImageView = view.findViewById(R.id.recordingAnimImageView)

        val animated = AnimatedVectorDrawableCompat.create(context!!, R.drawable.anim_record)
        animated?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                recordingAnimImageView.post { animated.start() }
            }
        })
        recordingAnimImageView.setImageDrawable(animated)
        animated?.start()

        fabPause.setOnClickListener {
            recorderPaused = if (recorderPaused) {
                fabPause.setImageDrawable(resources.getDrawable(R.drawable.ic_pause, context?.theme))
                recorder.resume()
                false
            } else {
                fabPause.setImageDrawable(resources.getDrawable(R.drawable.ic_resume, context?.theme))
                recorder.pause()
                true
            }
        }
        fabStop.setOnClickListener {
            recorder.stop()
            recorder.release()
            edtAudioName.requestFocus()
            showOrHideSoftKeyboard(true)
            fabSave.visibility = View.VISIBLE
            fabPause.visibility = View.GONE
            fabStop.visibility = View.GONE
        }
        checkDirectory()
        val fileName = "REC-${getCurrentDateTime()}.3gp"
        val path = "$FOLDER_PATH/$fileName"
        startRecording(path)

        edtAudioName.setText(fileName.removeSuffix(".3gp"))
        edtAudioName.setSelection(edtAudioName.text.length)
        fabSave.setOnClickListener {
            showOrHideSoftKeyboard(false)
            val fName = edtAudioName.text.toString().trim()
            if (fName.isNotEmpty()) {
                val newFile = File(FOLDER_PATH, "$fName.3gp")
                val oldFile = File(FOLDER_PATH, fileName)
                oldFile.renameTo(newFile)
                dismiss()
            }
        }
        return view
    }

    private fun startRecording(filePath: String) {
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(filePath)
        }
        recorder.prepare()
        recorder.start()
    }

    private fun getCurrentDateTime() = SimpleDateFormat("dd-MMM-yyyy-hh-mm-ss-Sa", Locale.ENGLISH).format(Date())

    private fun checkDirectory() {
        val file = File(FOLDER_PATH)
        if (!file.exists()) {
            file.mkdir()
        }
    }

    private fun showOrHideSoftKeyboard(show: Boolean) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            imm.toggleSoftInputFromWindow(view?.findViewById<LinearLayout>(R.id.frag_linear_layout)?.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0)
        } else {
            imm.toggleSoftInputFromWindow(view?.findViewById<LinearLayout>(R.id.frag_linear_layout)?.applicationWindowToken, InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }

}
