package io.chthonic.price_converter

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.github.salomonbrys.kodein.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.chthonic.price_converter.business.observer.CalculatorObservers
import io.chthonic.price_converter.business.observer.ExchangeObservers
import io.chthonic.price_converter.business.service.CalculatorService
import io.chthonic.price_converter.business.service.DroidPermissionsService
import io.chthonic.price_converter.business.service.ExchangeService
import io.chthonic.price_converter.business.service.StateService
import io.chthonic.price_converter.data.model.CalculatorSerializableState
import io.chthonic.price_converter.data.model.ExchangeState

/**
 * Created by jhavatar on 4/2/2018.
 */
fun depInject(app: Application): Kodein {
    return Kodein {
        bind<Application>() with instance(app)
        bind<Context>() with instance(app.applicationContext)
        bind<Resources>() with instance(app.applicationContext.resources)
        bind<StateService>() with singleton{ StateService() }
        bind<ExchangeObservers>() with provider { ExchangeObservers() }
        bind<CalculatorObservers>() with provider { CalculatorObservers() }
        bind<ExchangeService>() with singleton{ ExchangeService(instance(), instance(), instance(), instance()) }
        bind<CalculatorService>() with singleton{ CalculatorService(instance(), instance(), instance(), instance()) }
        bind<DroidPermissionsService>() with singleton{ DroidPermissionsService(instance()) }
        bind<Moshi>() with singleton {
            Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
        }
        bind<JsonAdapter<CalculatorSerializableState>>() with singleton {
            val moshi: Moshi = instance()
            moshi.adapter<CalculatorSerializableState>(CalculatorSerializableState::class.java)
        }
        bind<JsonAdapter<ExchangeState>>() with singleton {
            val moshi: Moshi = instance()
            moshi.adapter<ExchangeState>(ExchangeState::class.java)
        }
        bind<SharedPreferences>() with singleton {
            app.applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        }
    }
}