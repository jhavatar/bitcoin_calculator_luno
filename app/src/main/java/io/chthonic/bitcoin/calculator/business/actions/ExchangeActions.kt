package io.chthonic.bitcoin.calculator.business.actions

import com.yheriatovych.reductor.Action
import com.yheriatovych.reductor.annotations.ActionCreator
import io.chthonic.bitcoin.calculator.data.model.TickerLot

/**
 * Created by jhavatar on 3/27/2018.
 */
@ActionCreator
interface ExchangeActions {
    companion object {
        const val UPDATE_TICKERS: String = "UPDATE_TICKERS"
    }

    @ActionCreator.Action(UPDATE_TICKERS)
    fun updateTickers(tickerLot: TickerLot): Action
}
