package io.chthonic.price_converter.business.actions

import com.yheriatovych.reductor.Action
import com.yheriatovych.reductor.annotations.ActionCreator
import io.chthonic.price_converter.data.model.Ticker
import io.chthonic.price_converter.data.model.TickerLot

/**
 * Created by jhavatar on 3/27/2018.
 */
@ActionCreator
interface ExchangeActions {
    companion object {
        const val UPDATE_TICKERS: String = "UPDATE_TICKERS"
        const val SET_TARGET_TICKER: String = "SET_TARGET_TICKER"
        const val SWITCH_CONVERT_TO_FIAT: String = "SWITCH_CONVERT_TO_FIAT"
    }

    @ActionCreator.Action(UPDATE_TICKERS)
    fun updateTickers(tickerLot: TickerLot): Action

    @ActionCreator.Action(SET_TARGET_TICKER)
    fun setTargetTicker(tickerId: String): Action

    @ActionCreator.Action(SWITCH_CONVERT_TO_FIAT)
    fun switchConvertToFiat(convertToFiat: Boolean): Action
}
