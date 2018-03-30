package io.chthonic.price_converter

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.github.salomonbrys.kodein.*
import io.chthonic.price_converter.business.observer.CalculatorObservers
import io.chthonic.price_converter.business.observer.ExchangeObservers
import io.chthonic.price_converter.business.service.*
import io.chthonic.price_converter.utils.DebugUtils
import io.chthonic.stash.Stash
import io.chthonic.stash.cache.LruStorageCache
import io.chthonic.stash.storage.SharedPrefsStorage
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

        val that = this
        kodein = Kodein {
            bind<Application>() with instance(that)
            bind<Context>() with instance(applicationContext)
            bind<Resources>() with instance(applicationContext.resources)
//            bind<RestClient>() with singleton{ RestClient() }
            bind<StateService>() with singleton{StateService()}
            bind<ExchangeObservers>() with provider { ExchangeObservers() }
            bind<CalculatorObservers>() with provider { CalculatorObservers() }
            bind<ExchangeService>() with singleton{ ExchangeService(instance(), instance()) }
            bind<CalculatorService>() with singleton{ CalculatorService(instance(), instance()) }
            bind<DroidPermissionsService>() with singleton{DroidPermissionsService(instance())}
            bind<TodoListService>() with singleton{TodoListService(instance(), instance())}
            bind<Stash>() with singleton {
                Stash.Builder(SharedPrefsStorage.Builder().name("stash").build(instance()))
                        .cache(LruStorageCache(100))
                        .build()
            }
        }

        if (DebugUtils.dontInitSinceAnalsing(this)) {
            return
        }
        Timber.plant(Timber.DebugTree())
        DebugUtils.install(this)
    }
}