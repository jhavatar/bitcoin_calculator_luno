package io.chthonic.price_converter.ui.fragment

import android.content.Context
import io.chthonic.mythos.mvp.MVPDispatcher
import io.chthonic.mythos.mvp.PresenterCacheLoaderCallback
import io.chthonic.price_converter.ui.presenter.TodoPresenter
import io.chthonic.price_converter.ui.vu.TodoVu

/**
 * Created by jhavatar on 2/25/2017.
 */
class TodoFragment : MVPFragment<TodoPresenter, TodoVu>() {

    companion object {
        val TAG: String by lazy {
            TodoFragment::class.java.simpleName
        }
        private val MVP_UID by lazy {
            TAG.hashCode()
        }
    }

    override fun createMVPDispatcher(): MVPDispatcher<TodoPresenter, TodoVu> {
        return MVPDispatcher(MVP_UID,
                PresenterCacheLoaderCallback<TodoPresenter>(this.activity as Context, { TodoPresenter() }),
                ::TodoVu)
    }

}