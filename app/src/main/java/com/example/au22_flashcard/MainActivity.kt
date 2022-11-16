package com.example.au22_flashcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.au22_flashcard.databinding.ActivityMainBinding
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private var currentWord : Word? = null
    private lateinit var wordList: MutableList<Word>
    private lateinit var list : Deferred<List<Word>>
    private lateinit var db : AppDatabase
    private lateinit var addWordButton: Button
    private lateinit var deleteButton: Button
    private lateinit var job: Job
    private lateinit var viewBinding: ActivityMainBinding

    private lateinit var wordView: TextView
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)

        wordView = findViewById(R.id.wordTextView)

        addWordButton = findViewById(R.id.addWordButton)
        deleteButton = findViewById(R.id.deleteButton)

        db = AppDatabase.getInstance(this)

        job = Job()

        list = getWordsAsync()

        wordView.setOnClickListener {
            revealTranslation()
        }

        addWordButton.setOnClickListener{
            val intent = Intent(this, AddWordActivity::class.java)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            removeWord()
        }


        launch {
            wordList = list.await().toMutableList()

            if (wordList.isEmpty()) {
                wordView.text = resources.getText(R.string.empty_list)
            }
            else {
                showNewWord()
            }
        }
    }

    private fun updateWords() {

        list = getWordsAsync()

        launch {
            wordList = list.await().toMutableList()
            showNewWord()
        }
    }




    private fun getWordsAsync() : Deferred<List<Word>> =
        async(Dispatchers.IO) {
            db.wordDao.getAll()
        }


    private fun revealTranslation() {
        if (currentWord != null) {
            wordView.text = currentWord?.english
        }
    }


    private fun showNewWord() {
        currentWord = getNewWord()

        val wordView = findViewById<TextView>(R.id.wordTextView)
        wordView.text = currentWord?.swedish
    }


    private fun getNewWord(): Word {
        if (wordList.isEmpty()) {
            return Word(0, resources.getText(R.string.empty_list).toString(), resources.getText(R.string.empty_list).toString())
        }
        else {
            val rnd = (0 until wordList.size).random()
            return wordList.removeAt(rnd)
        }
    }

    private fun removeWord() {
        val word = currentWord
        if (word != null && wordList.size >= 1) {
            launch(Dispatchers.IO) {
                db.wordDao.delete(word)
            }

            updateWords()
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP && wordList.isNotEmpty() ) {
            showNewWord()
        }
        return true
    }

    override fun onRestart() {
        super.onRestart()
        updateWords()

    }
}

// 1. skapa en ny aktivitet där ett nytt ord får skrivas in. Check
// 2. spara det nya ordet i databasen. Check

// 3. i MainActivity läs in alla ord från databasen. Check

// (använd coroutiner när ni läser och skriver till databasen)