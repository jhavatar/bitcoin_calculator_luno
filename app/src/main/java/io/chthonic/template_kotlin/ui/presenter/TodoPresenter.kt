package io.chthonic.template_kotlin.ui.presenter

import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.chthonic.template_kotlin.App
import io.chthonic.template_kotlin.business.service.TodoListService
import io.chthonic.template_kotlin.data.model.TodoItem
import io.chthonic.template_kotlin.ui.model.TodoListChange
import io.chthonic.template_kotlin.ui.vu.TodoVu
import timber.log.Timber

/**
 * Created by jhavatar on 2/25/2017.
 */
class TodoPresenter(kodein: Kodein = App.kodein) : io.chthonic.template_kotlin.ui.presenter.BasePresenter<TodoVu>() {

    val todoService: TodoListService by App.kodein.lazy.instance<TodoListService>()

    override fun onLink(vu: TodoVu, inState: Bundle?, args: Bundle) {
        super.onLink(vu, inState, args)

        rxSubs.add(vu.onAddItemClick
                .subscribe({
                    Timber.d("on fab click")
                    vu.showAddTodo()
                }, {t: Throwable ->
                    Timber.e(t, "onAddItemClick failed")
                }))

        rxSubs.add(vu.onAddTodoItem
                .subscribe({title: String ->
                    Timber.d("add item $title")
                    todoService.addItem(title)

                }, {t: Throwable ->
                    Timber.e(t, "onAddTodoItem failed")
                }))

        rxSubs.add(vu.onTodoItemChanged
                .subscribe({ listChanged: TodoListChange ->
                    Timber.d("change item $listChanged")
                    todoService.updateItem(listChanged.pos, listChanged.item)
                }, {t: Throwable ->
                Timber.e(t, "onTodoItemChanged failed")})
        )

        vu.updateTodoList(todoService.todoListState)

        rxSubs.add(todoService.todoChangeObserver.subscribe({todoList: List<TodoItem> ->
            Timber.d("todoList = $todoList")
            vu.updateTodoList(todoList)
        }, {t: Throwable ->
            Timber.e(t, "stateChange failed")
        }))
    }
}