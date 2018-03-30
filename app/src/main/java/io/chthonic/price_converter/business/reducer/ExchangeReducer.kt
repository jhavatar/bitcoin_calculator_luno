package io.chthonic.price_converter.business.reducer

import com.yheriatovych.reductor.Reducer
import com.yheriatovych.reductor.annotations.AutoReducer
import io.chthonic.price_converter.business.actions.ExchangeActions
import io.chthonic.price_converter.data.model.ExchangeState
import io.chthonic.price_converter.data.model.TickerLot
import timber.log.Timber
import java.math.BigDecimal

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
        return ExchangeState(mapOf())
    }


    @AutoReducer.Action(
            value = ExchangeActions.UPDATE_TICKERS,
            from = ExchangeActions::class)
    fun updateTickers(state: ExchangeState, tickerLot: TickerLot): ExchangeState  {
        Timber.d("updateTickers $tickerLot")
        return state.copy(tickers = tickerLot.tickers.associateBy ( {it.pair}, {it} ))
    }
}