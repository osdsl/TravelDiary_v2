package com.example.traveldiary

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val appContext = context.applicationContext
    private val dbFile by lazy { appContext.getDatabasePath(DATABASE_NAME) }

    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 2
        private const val ASSETS_DB_PATH = "databases/notes.db"
        // Константы для структуры БД
        private const val TABLE_NOTES = "Notes"
        private const val COLUMN_TEXT = "Text"
    }

    init {
        if (!isDatabaseExists()) {
            copyDatabaseFromAssets()
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Не требуется, так как база предустановлена
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Логика обновления схемы при изменении версии
    }

    private fun isDatabaseExists(): Boolean {
        return dbFile.exists() && dbFile.length() > 0
    }

    private fun copyDatabaseFromAssets() {
        try {
            // Создаем родительские директории
            dbFile.parentFile?.mkdirs()

            // Копируем базу из assets
            appContext.assets.open(ASSETS_DB_PATH).use { inputStream ->
                FileOutputStream(dbFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                    Log.d("DB", "Database copied from assets")
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("Error copying database from assets", e)
        }
    }

    fun backupDatabase(backupDirName: String = "backup") {
        try {
            val backupDir = File(appContext.getExternalFilesDir(null), backupDirName)
            if (!backupDir.exists()) backupDir.mkdirs()

            val backupFile = File(backupDir, DATABASE_NAME)
            dbFile.copyTo(backupFile, overwrite = true)

            Log.d("DB", "Backup created: ${backupFile.absolutePath}")
        } catch (e: IOException) {
            Log.e("DB", "Backup failed: ${e.message}")
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        return try {
            super.getWritableDatabase()
        } catch (e: SQLiteException) {
            Log.e("DB", "Error opening writable database: ${e.message}")
            throw e
        }
    }

    // Пример метода для работы с данными
    fun getRecentNotes(limit: Int = 5): List<String> {
        val notes = mutableListOf<String>()
        val db = readableDatabase

        db.rawQuery(
            "SELECT Text FROM notes ORDER BY ID DESC LIMIT ?",
            arrayOf(limit.toString())
        ).use { cursor ->
            val textIndex = cursor.getColumnIndex("Text")
            if (textIndex == -1) return emptyList()

            while (cursor.moveToNext()) {
                notes.add(cursor.getString(textIndex))
            }
        }
        return notes
    }

    fun addTestNote(text: String) {
        val db = writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_TEXT, text)
            }
            db.insert(TABLE_NOTES, null, values)
            Log.d("DB", "Добавлена тестовая заметка: '$text'")
        } catch (e: Exception) {
            Log.e("DB", "Ошибка добавления заметки: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun addNote(text: String): Boolean {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TEXT, text.trim())
            }
            db.insert(TABLE_NOTES, null, values)
            true
        } catch (e: Exception) {
            Log.e("DB", "Ошибка сохранения: ${e.message}")
            false
        }
    }


    override fun close() {
        super.close()
        Log.d("DB", "Database connection closed")
    }
}