package com.example.taskmanagerapp.model


// This data class represents a single task in our app
// Each task has a unique id, a title, a description, and a completion status
data class Task(
    val id: String,           // Unique ID for each task (I'll use timestamp)
    val title: String,        // Short title of the task
    val description: String,  // Optional longer description
    val isCompleted: Boolean = false  // Default is not completed
)