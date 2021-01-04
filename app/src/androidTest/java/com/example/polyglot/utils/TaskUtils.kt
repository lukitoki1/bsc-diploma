package com.example.polyglot.utils

import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.text.Text
import java.util.concurrent.Callable

abstract class TaskUtils {
    companion object {
        fun isTaskComplete(task: Task<Text>?): Callable<Boolean> {
            return Callable {
                task?.isComplete ?: true
            }
        }
    }
}