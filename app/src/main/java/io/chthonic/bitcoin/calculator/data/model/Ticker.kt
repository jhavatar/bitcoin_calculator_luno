package io.chthonic.bitcoin.calculator.data.model

/**
 * Created by jhavatar on 3/25/2018.
 */
data class Ticker(val timestamp: Long,
                  val bid: String,
                  val ask: String,
                  val last_trade: String,
                  val rolling_24_hour_volume: String,
                  val pair: String) {

    val code: String
        get() {
            val c = pair.replace(CryptoCurrency.Bitcoin.code, "")
            return if (c.isNullOrEmpty()) CryptoCurrency.Bitcoin.code else c
        }
}