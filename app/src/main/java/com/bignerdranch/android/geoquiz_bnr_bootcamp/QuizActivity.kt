package com.bignerdranch.android.geoquiz_bnr_bootcamp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import Question
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_quiz.*

const val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.geoquiz_bnr_bootcamp.answer_is_true"
const val REQUEST_CHEAT_CODE = 0

class QuizActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var cheatButton: Button
    private val KEY_INDEX = "index"
    private val IS_CHEATER = "is_cheater"
    private var isCheater = false

    private val questionBank = listOf(
        Question(R.string.question_NYC, false, R.string.hint_NYC),
        Question(R.string.question_africa, true, R.string.hint_africa),
        Question(R.string.question_asia, true, R.string.hint_asia),
        Question(R.string.question_atl, false, R.string.hint_atl),
        Question(R.string.question_australia, true, R.string.hint_australia),
        Question(R.string.question_colombia, false, R.string.hint_colombia),
        Question(R.string.question_oceans, true, R.string.hint_oceans)
    )

    private var currentIndex = 0;

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ANNA", "onCreate(Bundle) called. Bundle = " + savedInstanceState)
        setContentView(R.layout.activity_quiz)
        currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        isCheater = savedInstanceState?.getBoolean(IS_CHEATER, false) ?: false

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }


        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % questionBank.size
            isCheater = false

            if (currentIndex > 6){
                currentIndex = 0
            }
            updateQuestion()

        }

        prevButton.setOnClickListener {
            currentIndex = (currentIndex - 1) % questionBank.size
            isCheater = false

            if (currentIndex < 0){
                currentIndex = 6
            }
            updateQuestion()

        }

        cheatButton.setOnClickListener { view: View ->
            val answerIsTrue = questionBank[currentIndex].answer
            val intent = CheatActivity.newIntent(this@QuizActivity, answerIsTrue)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options = ActivityOptions.makeClipRevealAnimation(view, 0,0, view.width, view.height)
                startActivityForResult(intent, REQUEST_CHEAT_CODE, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CHEAT_CODE)
            }
        }

        questionTextView.setOnClickListener{
            Toast.makeText(this, questionBank[currentIndex].hint, Toast.LENGTH_LONG).show()
        }

        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CHEAT_CODE) {
            isCheater = data?.getBooleanExtra(EXTRA_ANSWER_IS_SHOWN, false) ?: false
        }
    }

    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        question_text_view.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questionBank[currentIndex].answer
        val messageResId = when {
            isCheater -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i("ANNA", "onSaveInstanceState" + savedInstanceState)
        savedInstanceState.putInt(KEY_INDEX, currentIndex)
        savedInstanceState.putBoolean(IS_CHEATER, isCheater)

    }
}
