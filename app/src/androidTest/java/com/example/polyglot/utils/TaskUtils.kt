package com.example.polyglot.utils

import com.google.android.gms.tasks.Task
import java.util.concurrent.Callable

abstract class TaskUtils {
    companion object {
        fun<T> isTaskComplete(task: Task<T>?): Callable<Boolean> {
            return Callable {
                task?.isComplete ?: true
            }
        }
    }
}