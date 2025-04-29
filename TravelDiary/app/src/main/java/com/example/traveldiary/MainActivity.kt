package com.example.traveldiary

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnMapPress = findViewById<Button>(R.id.btn_map)
        btnMapPress.setOnClickListener{
            val intent = Intent(this, Map::class.java)
            startActivity(intent)
        }

        val btnNotesPress = findViewById<Button>(R.id.btn_create_note)
        btnNotesPress.setOnClickListener{
            val intent = Intent(this, Notes::class.java)
            startActivity(intent)
        }

        textView = findViewById(R.id.tv_notes_list)
        dbHelper = DatabaseHelper(this)

        loadRecentNotes()

    }

    private fun loadRecentNotes() {
        lifecycleScope.launch {
            try {
                // Проверка пустой базы
                if (dbHelper.getRecentNotes().isEmpty()) {
                    withContext(Dispatchers.IO) {
                        dbHelper.addTestNote("Тестовая заметка 1")
                        dbHelper.addTestNote("Тестовая заметка 2")
                        dbHelper.addTestNote("Тестовая заметка 3")
                    }
                }

                // Повторная загрузка
                val notes = withContext(Dispatchers.IO) {
                    dbHelper.getRecentNotes()
                }

                // Обновляем UI
                if (notes.isEmpty()) {
                    textView.text = getString(R.string.no_notes)
                    return@launch
                }

                val formattedText = notes.joinToString("\n\n")
                textView.text = formattedText

            } catch (e: Exception) {
                Log.e("MainActivity", "Ошибка загрузки заметок: ${e.message}")
                textView.text = getString(R.string.load_error)
                withContext(Dispatchers.Main) {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.load_error_details, e.message ?: "Неизвестная ошибка"),
                        Snackbar.LENGTH_LONG
                    ).setAction("Повторить") { loadRecentNotes() }.show()
                }
            }
        }
    }

    override fun onDestroy() {
        // Сохраняем резервную копию и закрываем соединение
        lifecycleScope.launch(Dispatchers.IO) {
            dbHelper.backupDatabase()
            dbHelper.close()
        }
        super.onDestroy()
    }
}