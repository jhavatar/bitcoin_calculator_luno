package io.chthonic.bitcoin.calculator.ui.vu

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import io.chthonic.mythos.mvp.Vu
import io.chthonic.bitcoin.calculator.R
import io.chthonic.bitcoin.calculator.ui.activity.BaseActivity
import timber.log.Timber

/**
 * Created by jhavatar on 3/7/17.
 */
abstract class BaseVu(layoutInflater: LayoutInflater, activity: Activity, fragment: Fragment?, parentView: ViewGroup?) :
        Vu(layoutInflater, activity, fragment, parentView) {

    val baseActivity: BaseActivity
        get() = activity as BaseActivity


    private val loadingIndicator: PopupWindow by lazy {
        val inflator = LayoutInflater.from(activity)
        val loadingView = inflator.inflate(R.layout.layout_loading, null)
        val dimen = activity.resources.getDimensionPixelSize(R.dimen.loading_container_dimen)
        val popup = PopupWindow(dimen, dimen)
        popup.contentView = loadingView
        popup
    }

    fun showLoading() {
        rootView.post{
            try {
                if (!loadingIndicator.isShowing) {
                    loadingIndicator.showAtLocation(rootView, Gravity.CENTER, 0, 0)
                }

            } catch (t: Throwable) {
                Timber.w(t, "showLoading failed.")
            }
        }
    }

    open fun hideLoading() {
        try {
            if (loadingIndicator.isShowing) {
                loadingIndicator.dismiss()
            }

        } catch (t: Throwable) {
            Timber.w(t, "hideLoading failed.")
        }
    }

    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
    }
}