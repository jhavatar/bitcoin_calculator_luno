package io.chthonic.price_converter.ui.vu

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.jakewharton.rxbinding2.view.RxView
import io.chthonic.mythos.mvp.FragmentWrapper
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.TodoItem
import io.chthonic.price_converter.ui.adapter.TodoListAdapter
import io.chthonic.price_converter.ui.model.TodoListChange
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.vu_todo.view.*


/**
 * Created by jhavatar on 2/25/2017.
 */
class TodoVu(layoutInflater: LayoutInflater,
             activity: Activity,
             fragmentWrapper: FragmentWrapper? = null,
             parentView: ViewGroup? = null):
        BaseVu(layoutInflater,
                activity = activity,
                fragmentWrapper = fragmentWrapper,
                parentView = parentView) {

    var adapter: TodoListAdapter? = null

    val onAddItemClick: Observable<Any> by lazy {
        RxView.clicks(rootView.fab)
    }

    private val todoItemChangePublisher: PublishSubject<TodoListChange> by lazy {
        PublishSubject.create<TodoListChange>()
    }
    val onTodoItemChanged: Observable<TodoListChange> by lazy {
        todoItemChangePublisher.hide()
    }

    private val addTodoItemPublisher : PublishSubject<String> by lazy {
        PublishSubject.create<String>()
    }
    val onAddTodoItem: Observable<String> by lazy {
        addTodoItemPublisher.hide()
    }

    override fun getRootViewLayoutId(): Int {
        return R.layout.vu_todo
    }

    override fun onCreate() {
        super.onCreate()

        rootView.todo_recycler_view.layoutManager = LinearLayoutManager(activity)
        adapter = TodoListAdapter(todoItemChangePublisher)
        rootView.todo_recycler_view.adapter = adapter
    }

    fun updateTodoList(todoList: List<TodoItem>) {
        if (adapter!!.items != todoList) {
            adapter!!.items = ArrayList(todoList)
            adapter!!.notifyDataSetChanged()
        }
    }

    fun showAddTodo() {
        // Set up the input
        val input = EditText(activity)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        AlertDialog.Builder(activity)
                .setTitle("Add Todo")
                .setView(input)
                .setNegativeButton("Cancel", {dialog: DialogInterface, which: Int ->
                })
                .setPositiveButton("Add", {dialog: DialogInterface, which: Int ->
                    addTodoItemPublisher.onNext(input.text.toString())
                })
                .show()
    }
}