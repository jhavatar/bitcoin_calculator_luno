package io.chthonic.price_converter.business.reducer

import com.yheriatovych.reductor.Reducer
import com.yheriatovych.reductor.annotations.AutoReducer
import io.chthonic.price_converter.business.actions.CalculatorActions
import io.chthonic.price_converter.data.model.CalculatorState
import timber.log.Timber
import java.math.BigDecimal

/**
 * Created by jhavatar on 3/30/2018.
 */
@AutoReducer
abstract class CalculatorReducer : Reducer<CalculatorState> {

    companion object {
        fun create(): CalculatorReducer {
            return CalculatorReducerImpl() //Note: usage of generated class
        }
    }

    @AutoReducer.InitialState
    internal fun initialState(): CalculatorState {
        return CalculatorState(null, true, BigDecimal(0))
    }

    @AutoReducer.Action(
            value = CalculatorActions.SET_TARGET_TICKER,
            from = CalculatorActions::class)
    fun setTargetTicker(state: CalculatorState, tickerId: String): CalculatorState  {
        Timber.d("setTargetTicker $tickerId")
        return state.copy(targetTicker = tickerId)
    }

    @AutoReducer.Action(
            value = CalculatorActions.SWITCH_CONVERT_DIRECTION,
            from = CalculatorActions::class)
    fun switchConvertDirection(state: CalculatorState, convertToFiat: Boolean): CalculatorState  {
        Timber.d("switchConvertDirection $convertToFiat")

        return if (convertToFiat || (!convertToFiat && (state.targetTicker != null))) {
            state.copy(convertToFiat = convertToFiat)

        } else {
            state
        }
    }


    @AutoReducer.Action(
            value = CalculatorActions.UPDATE_SOURCE,
            from = CalculatorActions::class)
    fun updateSource(state: CalculatorState, source: BigDecimal): CalculatorState  {
        Timber.d("updateSource $source")
        return state.copy(source = source)
    }

    @AutoReducer.Action(
            value = CalculatorActions.SWITCH_CONVERT_DIRECTION_AND_UPDATE_SOURCE,
            from = CalculatorActions::class)
    fun switchConvertDirectionAndUpdateSource(state: CalculatorState, convertToFiat: Boolean, source: BigDecimal): CalculatorState {
        Timber.d("updateSourceAndSwitchConvertDirection: convertToFiat = $convertToFiat, source = $source")
        return state.copy(convertToFiat = convertToFiat, source = source)
    }
}