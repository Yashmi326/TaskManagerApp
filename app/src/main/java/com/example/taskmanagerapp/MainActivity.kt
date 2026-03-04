package com.example.taskmanagerapp


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.adapter.TaskAdapter
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Main screen — shows the list of all tasks
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var textEmptyState: TextView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        // Connect variables to views
        recyclerView = findViewById(R.id.recyclerViewTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        textEmptyState = findViewById(R.id.textEmptyState)
        toolbar = findViewById(R.id.toolbar)

        // Set toolbar as action bar
        setSupportActionBar(toolbar)

        // Set up RecyclerView
        setupRecyclerView()

        // Observe task list changes — UI updates automatically when data changes
        viewModel.tasks.observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)

            // Show empty state message if no tasks exist
            if (tasks.isEmpty()) {
                textEmptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                textEmptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        // FAB click — open AddEditTaskActivity in ADD mode
        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivity(intent)
        }
    }

    // Refresh task list every time we return to this screen
    override fun onResume() {
        super.onResume()
        viewModel.refreshTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            tasks = mutableListOf(),

            // Click task to edit it
            onTaskClick = { task ->
                val intent = Intent(this, AddEditTaskActivity::class.java).apply {
                    putExtra("TASK_ID", task.id)
                    putExtra("TASK_TITLE", task.title)
                    putExtra("TASK_DESCRIPTION", task.description)
                }
                startActivity(intent)
            },

            // Click delete button — show confirmation dialog
            onDeleteClick = { task ->
                MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete \"${task.title}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteTask(task.id)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },

            // Click checkbox to toggle completion
            onCompleteClick = { task ->
                viewModel.toggleTaskCompletion(task.id)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
    }
}