package com.example.simpletasks

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var store: TaskStore
    private lateinit var tasks: MutableList<Task>
    private lateinit var adapter: TaskAdapter
    private lateinit var input: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        store = TaskStore(this)
        tasks = store.load()
        adapter = TaskAdapter(this, tasks)

        input = findViewById(R.id.input_task)
        val addButton = findViewById<Button>(R.id.btn_add)
        val list = findViewById<ListView>(R.id.list_tasks)
        val emptyView = findViewById<TextView>(R.id.text_empty)

        list.adapter = adapter
        list.emptyView = emptyView

        addButton.setOnClickListener { addTask() }
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addTask()
                true
            } else {
                false
            }
        }

        // ضغطة قصيرة: تبديل حالة الإنجاز
        list.setOnItemClickListener { _, _, position, _ ->
            val task = tasks[position]
            tasks[position] = task.copy(done = !task.done)
            persistAndRefresh()
        }

        // ضغطة مطولة: حذف المهمة بعد التأكيد
        list.setOnItemLongClickListener { _, _, position, _ ->
            confirmDelete(position)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, MENU_CLEAR_DONE, Menu.NONE, R.string.clear_done)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == MENU_CLEAR_DONE) {
            val removed = tasks.removeAll { it.done }
            if (removed) {
                persistAndRefresh()
            } else {
                Toast.makeText(this, R.string.nothing_done, Toast.LENGTH_SHORT).show()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addTask() {
        val title = input.text.toString().trim()
        if (title.isEmpty()) return

        tasks.add(0, Task(id = System.currentTimeMillis(), title = title))
        input.text.clear()
        persistAndRefresh()
    }

    private fun confirmDelete(position: Int) {
        val task = tasks[position]
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_title)
            .setMessage(task.title)
            .setPositiveButton(R.string.delete) { _, _ ->
                tasks.removeAll { it.id == task.id }
                persistAndRefresh()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun persistAndRefresh() {
        store.save(tasks)
        adapter.notifyDataSetChanged()
    }

    private companion object {
        const val MENU_CLEAR_DONE = 1
    }
}