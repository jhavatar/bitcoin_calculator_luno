package io.chthonic.template_kotlin.ui.model

import io.chthonic.template_kotlin.data.model.TodoItem

/**
 * Created by jhavatar on 2/26/2017.
 */
data class TodoListChange(val item: TodoItem, val pos: Int)