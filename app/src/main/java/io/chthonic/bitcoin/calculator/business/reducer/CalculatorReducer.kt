package io.chthonic.bitcoin.calculator.business.reducer

import android.content.SharedPreferences
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.squareup.moshi.JsonAdapter
import com.yheriatovych.reductor.Reducer
import com.yheriatovych.reductor.annotations.AutoReducer
import io.chthonic.bitcoin.calculator.App
import io.chthonic.bitcoin.calculator.business.actions.CalculatorActions
import io.chthonic.bitcoin.calculator.data.model.CalculatorSerializableState
import io.chthonic.bitcoin.calculator.data.model.CalculatorState
import io.chthonic.bitcoin.calculator.utils.CalculatorUtils
import io.chthonic.bitcoin.calculator.business.reducer.CalculatorReducerImpl
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

    val prefs: SharedPreferences by App.kodein.lazy.instance<SharedPreferences>()
    val serializer: JsonAdapter<CalculatorSerializableState> by App.kodein.lazy.instance<JsonAdapter<CalculatorSerializableState>>()

    @AutoReducer.InitialState
    internal fun initialState(): CalculatorState {
        return CalculatorUtils.getPersistedCalculatorState(prefs, serializer, CalculatorState.getFactoryState())
    }

    @AutoReducer.Action(
            value = CalculatorActions.SET_TARGET_TICKER,
            from = CalculatorActions::class)
    fun setTargetTicker(state: CalculatorState, tickerCode: String): CalculatorState {
        Timber.d("setTicker setTicker = tickerCode = $tickerCode, state = $state")
        return state.copy(leftTickerCode = if (state.leftTickerIsSource) state.leftTickerCode else tickerCode,
                rightTickerCode = if (state.leftTickerIsSource) tickerCode else state.rightTickerCode)
    }

    @AutoReducer.Action(
            value = CalculatorActions.SWITCH_CONVERT_DIRECTION_AND_UPDATE_SOURCE,
            from = CalculatorActions::class)
    fun updateSourceDirectionAndSourceValue(state: CalculatorState, leftTickerIsSource: Boolean, sourceValue: BigDecimal): CalculatorState {
        Timber.d("updateSourceDirectionAndSourceValue: leftTickerIsSource = $leftTickerIsSource, source = $sourceValue")
        return state.copy(leftTickerIsSource = leftTickerIsSource, sourceValue = sourceValue)
    }

    @AutoReducer.Action(
            value = CalculatorActions.CLEAR,
            from = CalculatorActions::class)
    fun clear(state: CalculatorState): CalculatorState {
        Timber.d("clear")
        return CalculatorState.getFactoryState()
    }
}