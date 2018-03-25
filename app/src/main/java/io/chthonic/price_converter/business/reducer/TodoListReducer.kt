package io.chthonic.price_converter.business.reducer

import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.yheriatovych.reductor.Reducer
import com.yheriatovych.reductor.annotations.AutoReducer
import io.chthonic.stash.Stash
import io.chthonic.price_converter.App
import io.chthonic.price_converter.business.actions.TodoListActions
import io.chthonic.price_converter.data.model.TodoItem
import io.chthonic.price_converter.utils.TodoUtils
import timber.log.Timber

/**
 * Created by jhavatar on 2/26/2017.
 */
@AutoReducer
abstract class TodoListReducer : Reducer<List<TodoItem>> {

    companion object {
        fun create(): TodoListReducer {
            return TodoListReducerImpl() //Note: usage of generated class
        }
    }

    val stash: Stash by App.kodein.lazy.instance<Stash>()

    @AutoReducer.InitialState
    internal fun initialState(): List<TodoItem> {
        return TodoUtils.getPersistedLocation(stash)
    }


    @AutoReducer.Action(
            value = TodoListActions.ADD_ITEM,
            from = TodoListActions::class)
    fun addItem(state: List<@JvmWildcard TodoItem>, item: TodoItem): List<TodoItem>  {
        Timber.d("add item $item")
        return state.plus(item)
    }


    @AutoReducer.Action(
            value = TodoListActions.REMOVE_ITEM,
            from = TodoListActions::class)
    fun removeItem(state: List<@JvmWildcard TodoItem>, item: TodoItem): List<TodoItem>  {
        return state.minus(item)
    }


    @AutoReducer.Action(
            value = TodoListActions.UPDATE_ITEM,
            from = TodoListActions::class)
    fun updateItem(state: List<@JvmWildcard TodoItem>, pos: Int, item: TodoItem): List<TodoItem>  {
        val mutList: MutableList<TodoItem> = state.toMutableList()
        mutList[pos] = item
        return mutList.toList()
    }
}