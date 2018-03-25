package io.chthonic.template_kotlin.utils

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import okhttp3.OkHttpClient

/**
 * Created by jhavatar on 9/12/16.
 */
object DebugUtils {

    lateinit private var refwatcher: RefWatcher

    fun install(app: Application) {
        refwatcher = LeakCanary.install(app)
        Stetho.initialize(
                Stetho.newInitializerBuilder(app)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(app))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(app))
                        .build())


    }

    fun dontInitSinceAnalsing(app: Application): Boolean {
        return LeakCanary.isInAnalyzerProcess(app)
    }

    fun watchForLeaks(watchedReference: Any) {
        refwatcher.watch(watchedReference)
    }

    fun modifyHttpClient(clientBuilder: OkHttpClient.Builder) {
        clientBuilder.networkInterceptors().add(StethoInterceptor())
    }
}
