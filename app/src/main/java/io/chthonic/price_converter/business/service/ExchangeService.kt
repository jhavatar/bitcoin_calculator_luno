package io.chthonic.price_converter.business.service

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.yheriatovych.reductor.Actions
import io.chthonic.price_converter.business.actions.ExchangeActions
import io.chthonic.price_converter.business.observer.ExchangeObservers
import io.chthonic.price_converter.data.client.RestClient
import io.chthonic.price_converter.data.model.AppState
import io.chthonic.price_converter.data.model.ExchangeState
import io.chthonic.price_converter.data.model.Ticker
import io.chthonic.price_converter.data.rest.LunoApi
import io.chthonic.price_converter.utils.ExchangeUtils
import io.reactivex.Single

/**
 * Created by jhavatar on 3/25/2018.
 */
class ExchangeService(private val stateService: StateService,
                      prefs: SharedPreferences,
                      serializer: JsonAdapter<ExchangeState>,
                      val observers: ExchangeObservers) {

    companion object {
        const val BASE_URL = "https://api.mybitx.com/api/1/"
    }

    val state: ExchangeState
        get() = stateService.state.exchangeState

    private val stateChangePersister = object: StateService.AppStateChangePersister<ExchangeState>() {

        override fun persist(state: AppState) {
            ExchangeUtils.setPersistedExchangeState(state.exchangeState, prefs, serializer)
        }

        override fun shouldPersist(state: AppState, oldState: AppState?): Boolean {
            return (oldState == null) || (oldState.exchangeState != state.exchangeState)
        }
    }

    private val exchangeActions: ExchangeActions by lazy {
        Actions.from(ExchangeActions::class.java)
    }

    private val restClient: RestClient by lazy {
        RestClient(BASE_URL)
    }

    private val lunoApi: LunoApi by lazy {
        restClient.restAdapter.create(LunoApi::class.java)
    }

    init {
        observers.registerPublishers(stateService)
        stateService.addPersister(stateChangePersister)
    }

    fun fetchTickerLot(): Single<List<Ticker>> {
        return lunoApi.getTickers().map {
            stateService.dispatch(exchangeActions.updateTickers(it))
            it.tickers
        }
    }

}