package io.chthonic.price_converter.business.reducer

import android.content.SharedPreferences
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.squareup.moshi.JsonAdapter
import com.yheriatovych.reductor.Reducer
import com.yheriatovych.reductor.annotations.AutoReducer
import io.chthonic.price_converter.App
import io.chthonic.price_converter.business.actions.ExchangeActions
import io.chthonic.price_converter.data.model.ExchangeState
import io.chthonic.price_converter.data.model.TickerLot
import io.chthonic.price_converter.utils.ExchangeUtils
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

    val prefs: SharedPreferences by App.kodein.lazy.instance<SharedPreferences>()
    val serializer: JsonAdapter<ExchangeState> by App.kodein.lazy.instance<JsonAdapter<ExchangeState>>()

    @AutoReducer.InitialState
    internal fun initialState(): ExchangeState {
        return ExchangeUtils.getPersistedExchangeState(prefs, serializer, ExchangeState(mapOf()))
    }


    @AutoReducer.Action(
            value = ExchangeActions.UPDATE_TICKERS,
            from = ExchangeActions::class)
    fun updateTickers(state: ExchangeState, tickerLot: TickerLot): ExchangeState  {
        Timber.d("updateTickers $tickerLot")
        return state.copy(tickers = tickerLot.tickers.associateBy ( {it.code}, {it} ))
    }
}