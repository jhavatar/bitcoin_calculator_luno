package io.chthonic.price_converter.business.reducer

import com.yheriatovych.reductor.Reducer
import com.yheriatovych.reductor.annotations.AutoReducer
import io.chthonic.price_converter.business.actions.ExchangeActions
import io.chthonic.price_converter.data.model.Ticker
import timber.log.Timber

/**
 * Created by jhavatar on 3/27/2018.
 */
@AutoReducer
abstract class ExchangeReducer : Reducer<List<Ticker>> {

    companion object {
        fun create(): ExchangeReducer {
            return ExchangeReducerImpl() //Note: usage of generated class
        }
    }

    @AutoReducer.InitialState
    internal fun initialState(): List<Ticker> {
        return listOf()
    }


    @AutoReducer.Action(
            value = ExchangeActions.UPDATE_TICKERS,
            from = ExchangeActions::class)
    fun updateTickers(tickers: List<@JvmWildcard Ticker>, tickersTruth: List<@JvmWildcard Ticker>): List<Ticker>  {
        Timber.d("updateTickers $tickersTruth")
        return tickersTruth
    }
}