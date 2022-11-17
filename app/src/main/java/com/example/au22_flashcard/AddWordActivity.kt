package com.example.au22_flashcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.au22_flashcard.databinding.ActivityAddwordBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AddWordActivity : AppCompatActivity(), CoroutineScope {


    private lateinit var englishText: EditText
    private lateinit var swedishText: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button
    private lateinit var job: Job
    lateinit var db : AppDatabase
    lateinit var viewBinding: ActivityAddwordBinding
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addword)

        viewBinding = ActivityAddwordBinding.inflate(layoutInflater)
        englishText = findViewById(R.id.englishEditText)
        swedishText = findViewById(R.id.swedishEditText)
        submitButton = findViewById(R.id.submitButton)
        cancelButton = findViewById(R.id.cancelButton)

        job = Job()

        db = AppDatabase.getInstance(this)

        submitButton.setOnClickListener {
            addWordToDB()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun addWordToDB() {
        if (englishText.text.isEmpty() || swedishText.text.isEmpty()) {
            Toast.makeText(this, "Error: Enter text before submitting", Toast.LENGTH_LONG).show()
        }
        else {
            val english = englishText.text.toString().trim().uppercase()
            val swedish = swedishText.text.toString().trim().uppercase()

            launch(Dispatchers.IO) {
                val newWord = Word(0, english, swedish)
                db.wordDao.insert(newWord)
            }

            finish()
        }
    }
}