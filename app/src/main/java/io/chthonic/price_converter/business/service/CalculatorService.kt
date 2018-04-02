package io.chthonic.price_converter.business.service

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import com.yheriatovych.reductor.Actions
import io.chthonic.price_converter.business.actions.CalculatorActions
import io.chthonic.price_converter.business.observer.CalculatorObservers
import io.chthonic.price_converter.data.model.AppState
import io.chthonic.price_converter.data.model.CalculatorSerializableState
import io.chthonic.price_converter.data.model.CalculatorState
import io.chthonic.price_converter.utils.CalculatorUtils
import timber.log.Timber

/**
 * Created by jhavatar on 3/30/2018.
 */
class CalculatorService(private val stateService: StateService,
                        prefs: SharedPreferences,
                        serializer: JsonAdapter<CalculatorSerializableState>,
                        val observers: CalculatorObservers) {

    val state: CalculatorState
        get() = stateService.state.calculatorState

    private val calculatorActions: CalculatorActions by lazy {
        Actions.from(CalculatorActions::class.java)
    }

    private val stateChangePersister = object: StateService.AppStateChangePersister<CalculatorSerializableState>() {

        override fun persist(state: AppState) {
            CalculatorUtils.setPersistedCalculatorState(state.calculatorState, prefs, serializer)
        }

        override fun shouldPersist(state: AppState, oldState: AppState?): Boolean {
            return (oldState == null) || (oldState.calculatorState != state.calculatorState)
        }
    }

    init {
        observers.registerPublishers(stateService)
        stateService.addPersister(stateChangePersister)
    }

    fun setTargetTicker(tickerCode: String) {
        stateService.dispatch(calculatorActions.setTargetTicker(tickerCode))
    }

    fun switchConvertDirection(convertToFiat: Boolean) {
        stateService.dispatch(calculatorActions.switchConvertDirection(convertToFiat))
    }

    fun switchConvertDirectionAndUpdateSource(convertToFiat: Boolean, source: String) {
        val sourceDecimal = try {
            source.toBigDecimal()
        } catch (t: Throwable) {
            Timber.w(t, "switchConvertDirectionAndUpdateSource: source $source is not numeric")
            return
        }

        stateService.dispatch(calculatorActions.switchConvertDirectionAndUpdateSource(convertToFiat, sourceDecimal))
    }
}