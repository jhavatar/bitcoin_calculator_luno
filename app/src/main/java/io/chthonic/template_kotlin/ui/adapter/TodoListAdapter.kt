package io.chthonic.template_kotlin.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.chthonic.template_kotlin.data.model.TodoItem
import io.chthonic.template_kotlin.ui.viewholder.TodoHolder
import io.chthonic.template_kotlin.ui.model.TodoListChange
import io.reactivex.subjects.PublishSubject

/**
 * Created by jhavatar on 2/26/2017.
 */
class TodoListAdapter(val itemChangePublisher: PublishSubject<TodoListChange>) : RecyclerView.Adapter<TodoHolder>() {

    var items: List<TodoItem> = emptyList()

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: TodoHolder, position: Int) {
        holder.update(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoHolder {
        return TodoHolder(TodoHolder.createView(parent), itemChangePublisher)
    }

}