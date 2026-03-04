package com.example.taskmanagerapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.model.Task

// Adapter connects our Task data to the RecyclerView UI
class TaskAdapter(
    private var tasks: MutableList<Task>,
    private val onTaskClick: (Task) -> Unit,       // called when task is clicked (edit)
    private val onDeleteClick: (Task) -> Unit,     // called when delete button clicked
    private val onCompleteClick: (Task) -> Unit    // called when checkbox clicked
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder holds references to all views in one task item
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textTaskTitle)
        val descriptionText: TextView = itemView.findViewById(R.id.textTaskDescription)
        val statusText: TextView = itemView.findViewById(R.id.textTaskStatus)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxComplete)
        val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDelete)
    }

    // This inflates (creates) the layout for each task item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    // This binds data to each task item view
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.titleText.text = task.title
        holder.descriptionText.text = task.description
        holder.checkBox.isChecked = task.isCompleted

        // Show status badge based on completion
        if (task.isCompleted) {
            // Completed style
            holder.titleText.paintFlags =
                holder.titleText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.statusText.text = "✓ Completed"
            holder.statusText.setBackgroundResource(R.drawable.bg_status)
            // Green color for completed
            (holder.statusText.background as? android.graphics.drawable.GradientDrawable)
                ?.setColor(android.graphics.Color.parseColor("#4CAF50"))
        } else {
            // Todo style
            holder.titleText.paintFlags =
                holder.titleText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.statusText.text = "● To Do"
            holder.statusText.setBackgroundResource(R.drawable.bg_status)
            // Blue color for todo
            (holder.statusText.background as? android.graphics.drawable.GradientDrawable)
                ?.setColor(android.graphics.Color.parseColor("#2196F3"))
        }

        // Click whole item to edit
        holder.itemView.setOnClickListener { onTaskClick(task) }

        // Click delete button to delete
        holder.deleteButton.setOnClickListener { onDeleteClick(task) }

        // Click checkbox to toggle completion
        holder.checkBox.setOnClickListener { onCompleteClick(task) }
    }

    override fun getItemCount(): Int = tasks.size

    // Call this to refresh the list when data changes
    fun updateTasks(newTasks: MutableList<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}