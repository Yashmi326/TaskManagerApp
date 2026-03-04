package com.example.taskmanagerapp.data

import android.content.Context
import com.example.taskmanagerapp.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TaskPreferencesHelper handles all local data storage using SharedPreferences.
 *
 * SECURE CODING PRACTICE 1 - Safe Local Storage:
 * We use MODE_PRIVATE which ensures that only this app can access
 * the stored data. No other app on the device can read our SharedPreferences.
 *
 * SECURE CODING PRACTICE 2 - No Sensitive Data Storage:
 * We only store non-sensitive task data (titles and descriptions).
 * No passwords, tokens, or personal information are ever stored.
 */
class TaskPreferencesHelper(context: Context) {

    // MODE_PRIVATE ensures data is only accessible by this app
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "task_prefs"  // Name of our preferences file
        private const val KEY_TASKS = "tasks"         // Key used to store task list
    }

    /**
     * Converts the task list to a JSON string and saves it to SharedPreferences.
     * Using .apply() instead of .commit() for asynchronous saving (better performance).
     */
    fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        prefs.edit().putString(KEY_TASKS, json).apply()
    }

    /**
     * Loads tasks from SharedPreferences and converts JSON back to a Task list.
     *
     * SECURE CODING PRACTICE #3 - Data Validation on Load:
     * If stored data is missing or corrupted, we safely return an empty list
     * instead of crashing the app. This prevents data corruption attacks.
     */
    fun loadTasks(): MutableList<Task> {
        val json = prefs.getString(KEY_TASKS, null)
            ?: return mutableListOf() // Safely return empty list if no data found
        return try {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            // If JSON is corrupted or unreadable, return empty list safely
            mutableListOf()
        }
    }
}