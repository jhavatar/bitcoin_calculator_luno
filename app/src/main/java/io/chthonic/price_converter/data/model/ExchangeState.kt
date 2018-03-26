package io.chthonic.price_converter.data.model

/**
 * Created by jhavatar on 3/27/2018.
 */
data class ExchangeState(val tickers: Map<String, Ticker>,
                         val targetTicker: String?,
                         val convertToFiat: Boolean) {
}