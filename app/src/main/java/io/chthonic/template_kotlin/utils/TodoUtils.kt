package io.chthonic.template_kotlin.utils

import com.squareup.moshi.Types
import io.chthonic.stash.Stash
import io.chthonic.stash.serializers.MoshiObjectSerializer
import io.chthonic.template_kotlin.data.model.TodoItem
import timber.log.Timber

/**
 * Created by jhavatar on 4/30/2017.
 */
object TodoUtils {

    const val PERSIST_KEY_NAME = "todo_list_state"

    private val todoListSerializer: MoshiObjectSerializer<List<TodoItem>> by lazy {
        MoshiObjectSerializer<List<TodoItem>>((Types.newParameterizedType(List::class.java, TodoItem::class.java)))
    }

    fun setPersistedTodoList(stash: Stash, todoList: List<TodoItem>) {
        Timber.d("persist todoList $todoList")
        stash.storage.putObject(PERSIST_KEY_NAME, todoList, todoListSerializer)
    }

    fun getPersistedLocation(stash: Stash): List<TodoItem> {
        Timber.d("retrieved persisted")
        val todoList = stash.getObject(PERSIST_KEY_NAME, listOf<List<TodoItem>>(), todoListSerializer) as List<TodoItem>
        Timber.d("retrieved persisted location $todoList")
        return todoList
    }
}