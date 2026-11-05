package com.example.simpletasks

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/** يحفظ المهام محلياً في SharedPreferences بصيغة JSON. */
class TaskStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): MutableList<Task> {
        val raw = prefs.getString(KEY_TASKS, null) ?: return mutableListOf()
        return try {
            val array = JSONArray(raw)
            MutableList(array.length()) { i ->
                val obj = array.getJSONObject(i)
                Task(
                    id = obj.getLong("id"),
                    title = obj.getString("title"),
                    done = obj.getBoolean("done")
                )
            }
        } catch (e: JSONException) {
            mutableListOf()
        }
    }

    fun save(tasks: List<Task>) {
        val array = JSONArray()
        for (task in tasks) {
            array.put(
                JSONObject()
                    .put("id", task.id)
                    .put("title", task.title)
                    .put("done", task.done)
            )
        }
        prefs.edit().putString(KEY_TASKS, array.toString()).apply()
    }

    private companion object {
        const val PREFS_NAME = "tasks_prefs"
        const val KEY_TASKS = "tasks_json"
    }
}
