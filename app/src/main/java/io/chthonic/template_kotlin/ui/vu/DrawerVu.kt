package io.chthonic.template_kotlin.ui.vu

import android.app.Activity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import io.chthonic.mythos.mvp.FragmentWrapper
import io.chthonic.template_kotlin.R
import timber.log.Timber

/**
 * Created by jhavatar on 3/8/17.
 */
abstract class DrawerVu(layoutInflater: LayoutInflater, activity: Activity, fragmentWrapper: FragmentWrapper?, parentView: ViewGroup?) :
        BaseVu(layoutInflater, activity, fragmentWrapper, parentView) {
    abstract val toolbar: Toolbar?


    override fun createRootView(inflater: LayoutInflater) : View {
        Timber.d("createRootView: parentView = $parentView")
        val viewStub: ViewStub = parentView!!.findViewById(R.id.drawer_activity_content) as ViewStub
        viewStub.layoutResource = getRootViewLayoutId()
        return viewStub.inflate()
    }
}