package com.example.traveldiary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Notes : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        editText = findViewById(R.id.editText)
        val btnSave = findViewById<Button>(R.id.btn_save_note)

        btnSave.setOnClickListener {
            saveNoteToDatabase()
        }

        val btnHomePress = findViewById<Button>(R.id.btn_return_to_main)
        btnHomePress.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnMapPress = findViewById<Button>(R.id.btn_map)
        btnMapPress.setOnClickListener{
            val intent = Intent(this, Map::class.java)
            startActivity(intent)
        }

    }

    private fun saveNoteToDatabase() {
        val noteText = editText.text.toString().trim()

        if (noteText.isEmpty()) {
            editText.error = "Введите текст заметки"
            return
        }

        lifecycleScope.launch {
            val isSuccess = withContext(Dispatchers.IO) {
                dbHelper.addNote(noteText)
            }

            withContext(Dispatchers.Main) {
                if (isSuccess) {
                    showSuccessMessage()
                    editText.text.clear()
                } else {
                    showErrorMessage()
                }
            }
        }
    }

    private fun showSuccessMessage() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "Заметка сохранена",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showErrorMessage() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "Ошибка сохранения",
            Snackbar.LENGTH_LONG
        ).setAction("Повторить") { saveNoteToDatabase() }.show()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

}