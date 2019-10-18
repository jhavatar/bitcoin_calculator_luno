package io.chthonic.bitcoin.calculator.data.model

/**
 * Created by jhavatar on 3/25/2018.
 */
data class Ticker(val timestamp: Long = 0,
                  val bid: String = "",
                  val ask: String = "",
                  val last_trade: String = "",
                  val rolling_24_hour_volume: String = "",
                  val pair: String = "") {

    val code: String
        get() = pair.replace(CryptoCurrency.Bitcoin.code, "")

    @delegate:Transient
    val isValid: Boolean by lazy {
        (timestamp > 0)
                && bid.isNotEmpty()
                && ask.isNotEmpty()
                && pair.isNotEmpty()
    }
}