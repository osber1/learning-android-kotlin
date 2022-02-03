package io.osvaldas.workoutapp.exercise

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.*
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.*
import io.osvaldas.workoutapp.*
import io.osvaldas.workoutapp.databinding.ActivityExcersiceBinding
import io.osvaldas.workoutapp.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import java.util.Locale.*

class ExerciseActivity : AppCompatActivity(), OnInitListener {
    private var binding: ActivityExcersiceBinding? = null

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    private val restTimerDuration: Long = 10
    private val exerciseTimerDuration: Long = 30

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcersiceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        tts = TextToSpeech(this, this)

        exerciseList = Constants.defaultExerciseList()

        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        setUpRestView()
        setupExerciseStatusRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        resetRestTimer()
        resetExerciseTimer()
        binding = null
        stopTts()
        stopMediaPlayer()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun customDialogForBackButton() {
        val dialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)

        dialog.setContentView(dialogBinding.root)
        dialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            dialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onInit(status: Int) {
        if (status == SUCCESS) {
            val result = tts?.setLanguage(US)
            if (result == LANG_MISSING_DATA || result == LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Selected language is not supported!")
            }
        } else {
            Log.e("TTS", "Initialization failed!")
        }
    }

    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(restTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                val leftTimer = 10 - restProgress
                binding?.progressBar?.progress = leftTimer
                binding?.tvTimer?.text = leftTimer.toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setUpExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar() {
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                val leftTimer = 30 - exerciseProgress
                binding?.progressBarExercise?.progress = leftTimer
                binding?.tvTimerExercise?.text = leftTimer.toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setUpRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    private fun setUpRestView() {
        playCompletionSound()

        binding?.flRestView?.visibility = VISIBLE
        binding?.tvTitle?.visibility = VISIBLE

        binding?.tvUpcomingLabel?.visibility = VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = VISIBLE

        binding?.tvExerciseName?.visibility = INVISIBLE
        binding?.ivImage?.visibility = INVISIBLE
        binding?.flExerciseView?.visibility = INVISIBLE

        binding?.tvUpcomingExerciseName?.text =
            exerciseList!![currentExercisePosition + 1].getName()

        resetRestTimer()
        setRestProgressBar()
    }

    private fun setUpExerciseView() {
        binding?.flRestView?.visibility = INVISIBLE
        binding?.tvTitle?.visibility = INVISIBLE

        binding?.tvUpcomingLabel?.visibility = INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = INVISIBLE

        binding?.tvExerciseName?.visibility = VISIBLE
        binding?.ivImage?.visibility = VISIBLE
        binding?.flExerciseView?.visibility = VISIBLE


        val exercise = exerciseList!![currentExercisePosition]
        speakOut(exercise.getName())

        binding?.tvExerciseName?.text = exercise.getName()
        binding?.ivImage?.setImageResource(exercise.getImage())

        resetExerciseTimer()
        setExerciseProgressBar()
    }

    private fun setupExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    private fun playCompletionSound() {
        try {
            player = MediaPlayer.create(this, R.raw.press_start)
            player?.isLooping = false
            player?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, QUEUE_FLUSH, null, "")
    }

    private fun resetExerciseTimer() {
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
    }

    private fun resetRestTimer() {
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
    }

    private fun stopTts() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
    }

    private fun stopMediaPlayer() {
        if (player != null) {
            player!!.stop()
        }
    }
}
