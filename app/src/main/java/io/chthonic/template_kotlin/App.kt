package io.chthonic.template_kotlin

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import io.chthonic.stash.Stash
import io.chthonic.stash.cache.LruStorageCache
import io.chthonic.stash.storage.SharedPrefsStorage
import io.chthonic.template_kotlin.business.service.DroidPermissionsService
import io.chthonic.template_kotlin.business.service.StateService
import io.chthonic.template_kotlin.business.service.TodoListService
import io.chthonic.template_kotlin.utils.DebugUtils

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
            bind<StateService>() with singleton{StateService()}
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
        DebugUtils.install(this)
    }
}