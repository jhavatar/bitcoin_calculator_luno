package io.chthonic.price_converter.business.reducer

import com.yheriatovych.reductor.Reducer
import com.yheriatovych.reductor.annotations.AutoReducer
import io.chthonic.price_converter.business.actions.ExchangeActions
import io.chthonic.price_converter.data.model.ExchangeState
import io.chthonic.price_converter.data.model.Ticker
import io.chthonic.price_converter.data.model.TickerLot
import timber.log.Timber

/**
 * Created by jhavatar on 3/27/2018.
 */
@AutoReducer
abstract class ExchangeReducer : Reducer<ExchangeState> {

    companion object {
        fun create(): ExchangeReducer {
            return ExchangeReducerImpl() //Note: usage of generated class
        }
    }

    @AutoReducer.InitialState
    internal fun initialState(): ExchangeState {
        return ExchangeState(mapOf(), null, false)
    }


    @AutoReducer.Action(
            value = ExchangeActions.UPDATE_TICKERS,
            from = ExchangeActions::class)
    fun updateTickers(state: ExchangeState, tickerLot: TickerLot): ExchangeState  {
        Timber.d("updateTickers $tickerLot")
        return state.copy(tickers = tickerLot.tickers.associateBy ( {it.pair}, {it} ))
    }

    @AutoReducer.Action(
            value = ExchangeActions.SET_TARGET_TICKER,
            from = ExchangeActions::class)
    fun setTargetTicker(state: ExchangeState, tickerId: String): ExchangeState  {
        Timber.d("setTargetTicker $tickerId")
        return state.copy(targetTicker = tickerId)
    }

    @AutoReducer.Action(
            value = ExchangeActions.SWITCH_CONVERT_TO_FIAT,
            from = ExchangeActions::class)
    fun switchConversionToFiat(state: ExchangeState, convertToFiat: Boolean): ExchangeState  {
        Timber.d("switchConversionToFiat $convertToFiat")
        return state.copy(convertToFiat = convertToFiat)
    }
}