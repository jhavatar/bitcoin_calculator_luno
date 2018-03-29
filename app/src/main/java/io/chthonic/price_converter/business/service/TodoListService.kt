package io.chthonic.price_converter.business.service

import com.yheriatovych.reductor.Actions
import io.chthonic.stash.Stash
import io.chthonic.price_converter.business.actions.TodoListActions
import io.chthonic.price_converter.business.observer.AppStateChangePublisher
import io.chthonic.price_converter.data.model.AppState
import io.chthonic.price_converter.data.model.TodoItem
import io.chthonic.price_converter.utils.TodoUtils
import io.reactivex.Observable

/**
 * Created by jhavatar on 4/30/2017.
 */
class TodoListService(val stateService: StateService, private val stash: Stash) {

    private val todoAction: TodoListActions by lazy {
        Actions.from(TodoListActions::class.java)
    }


    private val todoChangePublisher = object: AppStateChangePublisher<List<TodoItem>>() {
        override fun shouldPublish(state: AppState, oldState: AppState?): Boolean {
            return hasObservers()
                    && ((oldState == null) || (oldState.todoList != state.todoList))
        }

        override fun getPublishInfo(state: AppState): List<TodoItem> {
            return state.todoList
        }
    }

    val todoChangeObserver: Observable<List<TodoItem>>
        get() {
            return todoChangePublisher.observable
        }

    private val todoChangePersister = object: StateService.AppStateChangePersister<List<TodoItem>>() {

        override fun persist(state: AppState) {
            TodoUtils.setPersistedTodoList(stash, state.todoList)
        }

        override fun shouldPersist(state: AppState, oldState: AppState?): Boolean {
            return (oldState == null) || (oldState.todoList != state.todoList)
        }
    }

    val todoListState: List<TodoItem>
        get() = stateService.state.todoList

    init {
        stateService.addPublisher(todoChangePublisher)
        stateService.addPersister(todoChangePersister)
    }


    fun addItem(title: String) {
        stateService.dispatch(todoAction.addItem(TodoItem(title)))
    }

    fun updateItem(pos: Int, item: TodoItem) {
        stateService.dispatch(todoAction.updateItem(pos, item))
    }
}