package io.chthonic.template_kotlin.ui.vu

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.ViewGroup
import io.chthonic.mythos.mvp.FragmentWrapper
import io.chthonic.template_kotlin.R
import io.chthonic.template_kotlin.ui.fragment.TodoFragment
import kotlinx.android.synthetic.main.vu_main.view.*
import timber.log.Timber

/**
 * Created by jhavatar on 4/30/2017.
 */
class MainVu(inflater: LayoutInflater,
             activity: Activity,
             fragmentWrapper: FragmentWrapper? = null,
             parentView: ViewGroup? = null) :
        DrawerVu(inflater,
                activity = activity,
                fragmentWrapper = fragmentWrapper,
                parentView = parentView)  {

    override val toolbar: Toolbar?
        get() = rootView.toolbar


    override fun getRootViewLayoutId(): Int {
        return R.layout.vu_main
    }

    override fun onCreate() {
        super.onCreate()
        showTodoList()
    }

    fun showTodoList() {
        val todoListFragment: Fragment? = getFragment(TodoFragment.TAG)
        if (todoListFragment == null) {
            insertFragment(TodoFragment(), TodoFragment.TAG)
        }
    }

    private fun getFragment(tag: String): Fragment? {
        return (activity as AppCompatActivity).supportFragmentManager.findFragmentByTag(tag)
    }

    private fun insertFragment(fragment: Fragment, tag: String) {
        Timber.d("insertFragment $tag")
        (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment, tag)
                .commit()
    }

    private fun removeFragment(fragment: Fragment) {
        (activity as AppCompatActivity).supportFragmentManager.beginTransaction().remove(fragment).commit()
    }
}