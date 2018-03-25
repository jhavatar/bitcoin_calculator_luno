package io.chthonic.template_kotlin.ui.fragment

import android.support.v4.app.Fragment
import io.chthonic.template_kotlin.utils.DebugUtils


/**
 * Created by jhavatar on 3/7/17.
 */
abstract class BaseFragment: Fragment() {

    override fun onDestroyView() {
        super.onDestroyView()
        DebugUtils.watchForLeaks(this)
    }
}