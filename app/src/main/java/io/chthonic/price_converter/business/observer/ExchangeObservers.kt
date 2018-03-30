package io.chthonic.price_converter.business.observer

import io.chthonic.price_converter.data.model.AppState
import io.chthonic.price_converter.data.model.Ticker
import io.reactivex.Observable
import timber.log.Timber

/**
 * Created by jhavatar on 3/29/2018.
 */
class ExchangeObservers: AppStateChangeObservers() {

    private val tickersChangePublisher: AppStateChangePublisher<Map<String, Ticker>> by lazy {
        object : AppStateChangePublisher<Map<String, Ticker>>() {
            override fun getPublishInfo(state: AppState): Map<String, Ticker> {
                return state.exchangeState.tickers
            }

            override fun shouldPublish(state: AppState, oldState: AppState?): Boolean {
                Timber.d("tickersChangePublisher: oldState = ${oldState?.exchangeState}, newState = ${state.exchangeState}")
                return hasObservers() && (oldState?.exchangeState?.tickers != state.exchangeState.tickers)
            }
        }
    }

    val tickersChangeObserver: Observable<Map<String, Ticker>>
        get() = tickersChangePublisher.observable

    override val publishersToRegister: List<AppStateChangePublisher<*>> = listOf(tickersChangePublisher)

}