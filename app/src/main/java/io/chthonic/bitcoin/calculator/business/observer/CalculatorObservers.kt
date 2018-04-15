package io.chthonic.bitcoin.calculator.business.observer

import io.chthonic.bitcoin.calculator.data.model.AppState
import io.chthonic.bitcoin.calculator.data.model.CalculatorState
import io.reactivex.Observable
import timber.log.Timber

/**
 * Created by jhavatar on 3/30/2018.
 */
class CalculatorObservers: AppStateChangeObservers() {

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