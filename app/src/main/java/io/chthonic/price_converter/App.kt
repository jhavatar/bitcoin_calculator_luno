package io.chthonic.price_converter

import com.github.salomonbrys.kodein.Kodein
import io.chthonic.price_converter.utils.DebugUtils
import io.chthonic.template_kotlin.BaseApp
import timber.log.Timber

/**
 * Created by jhavatar on 3/2/17.
 */
class App : BaseApp() {
    companion object {
        lateinit var kodein: Kodein
            private set
    }

    override fun onCreate() {
        super.onCreate()

        kodein = depInject(this)

        if (DebugUtils.dontInitSinceAnalsing(this)) {
            return
        }
        Timber.plant(Timber.DebugTree())
        DebugUtils.install(this)
    }
}