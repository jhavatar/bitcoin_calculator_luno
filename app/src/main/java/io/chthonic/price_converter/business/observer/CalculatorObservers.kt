package io.chthonic.price_converter.business.observer

import io.chthonic.price_converter.data.model.AppState
import io.chthonic.price_converter.data.model.CalculatorState
import io.chthonic.price_converter.data.model.NullSafeWrapper
import io.chthonic.price_converter.data.model.Ticker
import io.reactivex.Observable
import timber.log.Timber

/**
 * Created by jhavatar on 3/30/2018.
 */
class CalculatorObservers: AppStateChangeObservers() {

//    private val targetTickerChangePublisher: AppStateChangePublisher<NullSafeWrapper<String>> by lazy {
//        object : AppStateChangePublisher<NullSafeWrapper<String>>() {
//            override fun getPublishInfo(state: AppState): NullSafeWrapper<String> {
//                return NullSafeWrapper(state.calculatorState.targetTicker)
//            }
//
//            override fun shouldPublish(state: AppState, oldState: AppState?): Boolean {
//                return hasObservers() && (oldState?.calculatorState?.targetTicker != state.calculatorState?.targetTicker)
//            }
//        }
//    }
//    val targetTickerChangeObserver: Observable<NullSafeWrapper<String>>
//        get() = targetTickerChangePublisher.observable


//    private val conversionDirectionChangePublisher: AppStateChangePublisher<Boolean> by lazy {
//        object : AppStateChangePublisher<Boolean>() {
//            override fun getPublishInfo(state: AppState): Boolean {
//                return state.calculatorState.convertToFiat
//            }
//
//            override fun shouldPublish(state: AppState, oldState: AppState?): Boolean {
//                return hasObservers() && (oldState?.calculatorState?.convertToFiat != state.calculatorState?.convertToFiat)
//            }
//        }
//    }
//    val conversionDirectionChangeObserver: Observable<NullSafeWrapper<String>>
//        get() = targetTickerChangePublisher.observable


    private val calculationChangePublisher: AppStateChangePublisher<CalculatorState> by lazy {
        object : AppStateChangePublisher<CalculatorState>() {
            override fun getPublishInfo(state: AppState): CalculatorState {
                return state.calculatorState
            }

            override fun shouldPublish(state: AppState, oldState: AppState?): Boolean {
                Timber.d("calculationChangePublisher: oldState = ${oldState?.calculatorState}, newState = ${state.calculatorState}")
                return hasObservers() && (oldState?.calculatorState != state.calculatorState)
            }
        }
    }
    val calculationChangeChangeObserver: Observable<CalculatorState>
        get() = calculationChangePublisher.observable


    override val publishersToRegister: List<AppStateChangePublisher<*>>
        get() = listOf(calculationChangePublisher)

}