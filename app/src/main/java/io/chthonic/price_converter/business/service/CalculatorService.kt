package io.chthonic.price_converter.business.service

import com.yheriatovych.reductor.Actions
import io.chthonic.price_converter.business.actions.CalculatorActions
import io.chthonic.price_converter.business.observer.CalculatorObservers
import io.chthonic.price_converter.data.model.CalculatorState
import timber.log.Timber
import java.math.BigDecimal

/**
 * Created by jhavatar on 3/30/2018.
 */
class CalculatorService(private val stateService: StateService, val observers: CalculatorObservers) {

    val state: CalculatorState
        get() = stateService.state.calculatorState

    private val calculatorActions: CalculatorActions by lazy {
        Actions.from(CalculatorActions::class.java)
    }

    init {
        observers.registerPublishers(stateService)
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