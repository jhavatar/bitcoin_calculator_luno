package io.chthonic.price_converter.data.model

import java.math.BigDecimal

/**
 * Created by jhavatar on 3/27/2018.
 */
data class ExchangeState(val tickers: Map<String, Ticker>) {
}