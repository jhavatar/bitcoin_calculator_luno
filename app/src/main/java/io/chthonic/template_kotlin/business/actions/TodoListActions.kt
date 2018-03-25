package io.chthonic.template_kotlin.business.actions

import com.yheriatovych.reductor.Action
import com.yheriatovych.reductor.annotations.ActionCreator
import io.chthonic.template_kotlin.data.model.TodoItem


/**
 * Created by jhavatar on 2/26/2017.
 */
@ActionCreator
interface TodoListActions {
    companion object {
        const val ADD_ITEM: String = "ADD_ITEM"
        const val REMOVE_ITEM: String = "REMOVE_ITEM"
        const val UPDATE_ITEM: String = "UPDATE_ITEM"
    }

    @ActionCreator.Action(ADD_ITEM)
    fun addItem(item: TodoItem): Action

    @ActionCreator.Action(REMOVE_ITEM)
    fun removeItem(item: TodoItem): Action

    @ActionCreator.Action(UPDATE_ITEM)
    fun updateItem(pos: Int, item: TodoItem): Action
}