package com.example.taskmanagerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

// This activity handles both ADDING a new task and EDITING an existing one
class AddEditTaskActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var editTextTitle: TextInputEditText
    private lateinit var editTextDescription: TextInputEditText
    private lateinit var buttonSave: MaterialButton
    private lateinit var toolbar: MaterialToolbar

    // If these are not null, we are in EDIT mode
    private var taskId: String? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        // Connect variables to views
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonSave = findViewById(R.id.buttonSaveTask)
        toolbar = findViewById(R.id.toolbarAddEdit)

        // Set up back button on toolbar
        toolbar.setNavigationOnClickListener {
            finish() // Go back to main screen
        }

        // Check if we received a task to edit
        // If intent has task data, we are in edit mode
        taskId = intent.getStringExtra("TASK_ID")
        val taskTitle = intent.getStringExtra("TASK_TITLE")
        val taskDescription = intent.getStringExtra("TASK_DESCRIPTION")

        if (taskId != null) {
            // EDIT MODE - fill in existing data
            isEditMode = true
            toolbar.title = "Edit Task"
            editTextTitle.setText(taskTitle)
            editTextDescription.setText(taskDescription)
        } else {
            // ADD MODE
            toolbar.title = "Add Task"
        }

        // Restore unsaved text after screen rotation
        // SavedInstanceState preserves UI state automatically with EditText IDs
        savedInstanceState?.let {
            editTextTitle.setText(it.getString("TITLE_KEY"))
            editTextDescription.setText(it.getString("DESC_KEY"))
        }

        // Save button click
        buttonSave.setOnClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        // SECURE CODING PRACTICE #4 - Input Validation:
        // Always trim whitespace and validate user input before saving.
        // This prevents saving empty or blank tasks accidentally.
        val title = editTextTitle.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        // Title is required — show error message if empty
        if (title.isEmpty()) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Please enter a task title",
                Snackbar.LENGTH_SHORT
            ).show()
            return // Stop here — do not save invalid data
        }

        if (isEditMode) {
            viewModel.updateTask(taskId!!, title, description) // Update existing task
        } else {
            viewModel.addTask(title, description) // Add new task
        }

        finish() // Return to main screen after saving
    }

    // Save current text input state in case of screen rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("TITLE_KEY", editTextTitle.text.toString())
        outState.putString("DESC_KEY", editTextDescription.text.toString())
    }
}