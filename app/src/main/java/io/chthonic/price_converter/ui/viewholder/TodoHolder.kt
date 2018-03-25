package io.chthonic.price_converter.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.TodoItem
import io.chthonic.price_converter.ui.model.TodoListChange
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.holder_todo.view.*


/**
 * Created by jhavatar on 2/26/2017.
 */
class TodoHolder(itemView: View, itemChangePublisher: PublishSubject<TodoListChange>) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun createView(parentView: ViewGroup): View {
            return LayoutInflater.from(parentView.context).inflate(R.layout.holder_todo, parentView, false)
        }
    }

    init {
        this.itemView.is_done.setOnCheckedChangeListener { compoundButton: CompoundButton, b : Boolean ->
            itemChangePublisher.onNext(TodoListChange(TodoItem(itemView.title.text as String, this.itemView.is_done.isChecked), adapterPosition))
        }
    }

    fun update(item: TodoItem) {
        this.itemView.title.text = item.title
        this.itemView.is_done.isChecked = item.isDone
    }
}