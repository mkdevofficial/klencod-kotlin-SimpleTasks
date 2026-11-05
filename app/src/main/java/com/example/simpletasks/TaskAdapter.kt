package com.example.simpletasks

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class TaskAdapter(
    context: Context,
    private val tasks: List<Task>
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = tasks.size

    override fun getItem(position: Int): Task = tasks[position]

    override fun getItemId(position: Int): Long = tasks[position].id

    override fun hasStableIds(): Boolean = true

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_task, parent, false)
        val task = tasks[position]

        val check = view.findViewById<CheckBox>(R.id.task_check)
        val title = view.findViewById<TextView>(R.id.task_title)

        check.isChecked = task.done
        title.text = task.title

        if (task.done) {
            title.paintFlags = title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            title.alpha = 0.5f
        } else {
            title.paintFlags = title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            title.alpha = 1f
        }
        return view
    }
}
