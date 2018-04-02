package io.chthonic.price_converter.business.reducer

import android.content.SharedPreferences
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.squareup.moshi.JsonAdapter
import com.yheriatovych.reductor.Reducer
import com.yheriatovych.reductor.annotations.AutoReducer
import io.chthonic.price_converter.App
import io.chthonic.price_converter.business.actions.CalculatorActions
import io.chthonic.price_converter.data.model.CalculatorSerializableState
import io.chthonic.price_converter.data.model.CalculatorState
import io.chthonic.price_converter.utils.CalculatorUtils
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
    fun setTargetTicker(state: CalculatorState, tickerCode: String): CalculatorState  {
        Timber.d("setTargetTicker $tickerCode")
        return state.copy(targetTicker = tickerCode)
    }

    @AutoReducer.Action(
            value = CalculatorActions.SWITCH_CONVERT_DIRECTION_AND_UPDATE_SOURCE,
            from = CalculatorActions::class)
    fun switchConvertDirectionAndUpdateSource(state: CalculatorState, convertToFiat: Boolean, source: BigDecimal): CalculatorState {
        Timber.d("updateSourceAndSwitchConvertDirection: convertToFiat = $convertToFiat, source = $source")
        return state.copy(convertToFiat = convertToFiat, source = source)
    }

    @AutoReducer.Action(
            value = CalculatorActions.CLEAR,
            from = CalculatorActions::class)
    fun clear(state: CalculatorState): CalculatorState {
        Timber.d("clear")
        return CalculatorState.getFactoryState()
    }
}