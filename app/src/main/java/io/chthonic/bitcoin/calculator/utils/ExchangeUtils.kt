package io.chthonic.bitcoin.calculator.utils

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import io.chthonic.bitcoin.calculator.data.model.ExchangeState
import io.chthonic.bitcoin.calculator.data.model.FiatCurrency
import io.chthonic.bitcoin.calculator.data.model.Ticker
import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext

/**
 * Created by jhavatar on 3/25/2018.
 */
object ExchangeUtils {

    private const val PERSIST_KEY_NAME = "exchange_state"

    private val codeToFiatCurrencyMap: Map<String, FiatCurrency> by lazy {
        FiatCurrency.values.associateBy( {it.code}, {it} )
    }

    fun getPersistedExchangeState(prefs: SharedPreferences, serializer: JsonAdapter<ExchangeState>, fallbackState: ExchangeState): ExchangeState {
        Timber.d("getPersistedExchangeState")
        return try {
            val json = prefs.getString(PERSIST_KEY_NAME, null)
            Timber.d("getPersistedExchangeState: json = $json")
            if (json != null) {
                try {
                    serializer.fromJson(json)?.let{ state ->
                        state.copy(tickers = state.tickers.filter {
                            it.value.isValid
                        })
                    } ?: fallbackState

                } catch (t: Throwable) {
                    Timber.e(t, "getPersistedExchangeState: fromJson failed")
                    prefs.edit().remove(PERSIST_KEY_NAME)
                    fallbackState
                }

            } else {
                fallbackState
            }

        } catch (t: Throwable) {
            Timber.e(t,"getPersistedExchangeState failed")
            fallbackState
        }
    }


    fun setPersistedExchangeState(exchangeState: ExchangeState, prefs: SharedPreferences, serializer: JsonAdapter<ExchangeState>) {
        Timber.d("setPersistedExchangeState $exchangeState")
        try {
            val json = serializer.toJson(exchangeState)
            if (!json.isNullOrEmpty()) {
                prefs.edit().putString(PERSIST_KEY_NAME, json).apply()
            }

        } catch (t: Throwable) {
            Timber.e(t,"setPersistedExchangeState failed")
        }
    }


    fun getFiatCurrencyForTicker(ticker: Ticker): FiatCurrency? {
        return getFiatCurrencyForTicker(ticker.code)
    }

    fun getFiatCurrencyForTicker(code: String): FiatCurrency? {
        return codeToFiatCurrencyMap[code]
    }

    fun isSupportedFiatCurrency(ticker: Ticker): Boolean {
        return getFiatCurrencyForTicker(ticker) != null
    }

    fun convertToFiat(bitcoinPrice: BigDecimal, ticker: Ticker): BigDecimal {
        Timber.d("convertToFiat: ticker = $ticker, bitcoinPrice = $bitcoinPrice")
        return bitcoinPrice.multiply(ticker.bid.toBigDecimal())
    }

    fun convertToBitcoin(fiatPrice: BigDecimal, ticker: Ticker): BigDecimal {
        Timber.d("convertToBitcoin: fiatPrice = $fiatPrice, ticker = $ticker")
        return fiatPrice.divide(ticker.ask.toBigDecimal(), MathContext.DECIMAL128)
    }

}