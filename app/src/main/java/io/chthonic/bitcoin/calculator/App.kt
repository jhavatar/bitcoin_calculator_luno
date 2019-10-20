package io.chthonic.bitcoin.calculator

import com.github.salomonbrys.kodein.Kodein
import com.mikepenz.iconics.Iconics
import io.chthonic.bitcoin.calculator.utils.DebugUtils
import timber.log.Timber

/**
 * Created by jhavatar on 3/2/17.
 */
class App : android.app.Application() {
    companion object {
        lateinit var kodein: Kodein
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // currency icons/emojis on all devices
        Iconics.init(applicationContext)

        kodein = depInject(this)

        if (DebugUtils.dontInitSinceAnalsing(this)) {
            return
        }
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else object: Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                // ignore
            }
        })
        DebugUtils.install(this)
    }
}