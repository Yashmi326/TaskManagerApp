package com.example.taskmanagerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskmanagerapp.data.TaskPreferencesHelper
import com.example.taskmanagerapp.model.Task

/**
 * TaskViewModel manages all task data and business logic.
 *
 * By extending AndroidViewModel, this ViewModel survives screen rotation.
 * This means tasks won't disappear when the user rotates their phone,
 * this is how we handle STATE MANAGEMENT as required by the assignment.
 *
 * The UI (MainActivity) observes LiveData and updates automatically
 * whenever task data changes — no manual refresh needed.
 */
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsHelper = TaskPreferencesHelper(application)

    // _tasks is MutableLiveData — only ViewModel can modify it (encapsulation)
    private val _tasks = MutableLiveData<MutableList<Task>>()

    // tasks is exposed as read-only LiveData to the UI
    val tasks: LiveData<MutableList<Task>> get() = _tasks

    // Automatically load tasks when ViewModel is first created
    init {
        loadTasks()
    }

    /** Load all tasks from SharedPreferences into LiveData */
    private fun loadTasks() {
        _tasks.value = prefsHelper.loadTasks()
    }

    /** Public function to reload tasks — called when returning to MainActivity */
    fun refreshTasks() {
        _tasks.value = prefsHelper.loadTasks()
    }

    /**
     * Adds a new task to the list.
     * Uses System.currentTimeMillis() as a unique ID — simple and effective.
     */
    fun addTask(title: String, description: String) {
        val currentList = _tasks.value ?: mutableListOf()
        val newTask = Task(
            id = System.currentTimeMillis().toString(),
            title = title,
            description = description
        )
        currentList.add(newTask)
        _tasks.value = currentList
        prefsHelper.saveTasks(currentList)
    }

    /**
     * Updates an existing task found by its unique ID.
     * Uses Kotlin's copy() function to create a modified version of the task.
     */
    fun updateTask(id: String, newTitle: String, newDescription: String) {
        val currentList = _tasks.value ?: mutableListOf()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(
                title = newTitle,
                description = newDescription
            )
            _tasks.value = currentList
            prefsHelper.saveTasks(currentList)
        }
    }

    /** Removes a task from the list by its unique ID */
    fun deleteTask(id: String) {
        val currentList = _tasks.value ?: mutableListOf()
        currentList.removeAll { it.id == id }
        _tasks.value = currentList
        prefsHelper.saveTasks(currentList)
    }

    /**
     * Toggles the completion status of a task.
     * Toggle means: if completed → make To Do, if To Do → make Completed.
     */
    fun toggleTaskCompletion(id: String) {
        val currentList = _tasks.value ?: mutableListOf()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(
                isCompleted = !currentList[index].isCompleted
            )
            _tasks.value = currentList
            prefsHelper.saveTasks(currentList)
        }
    }
}