package io.chthonic.bitcoin.calculator.utils

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter
import io.chthonic.bitcoin.calculator.data.model.CalculatorSerializableState
import io.chthonic.bitcoin.calculator.data.model.CalculatorState
import io.chthonic.bitcoin.calculator.data.model.ExchangeState
import io.chthonic.bitcoin.calculator.data.model.Ticker
import timber.log.Timber
import java.math.BigDecimal

/**
 * Created by jhavatar on 4/2/2018.
 */
object CalculatorUtils {

    private const val PERSIST_KEY_NAME = "calculator_state"

    fun getPersistedCalculatorState(prefs: SharedPreferences, serializer: JsonAdapter<CalculatorSerializableState>, fallbackState: CalculatorState): CalculatorState {
        Timber.d("getPersistedCalculatorState")
        return try {
            val json = prefs.getString(PERSIST_KEY_NAME, null)
            Timber.d("getPersistedCalculatorState: json = $json")
            if (json != null) {
                serializer.fromJson(json)?.toCalculatorState() ?: fallbackState

            } else {
                fallbackState
            }

        } catch (t: Throwable) {
            Timber.e(t,"getPersistedCalculatorState failed")
            fallbackState
        }
    }

    fun setPersistedCalculatorState(calculatorState: CalculatorState, prefs: SharedPreferences, serializer: JsonAdapter<CalculatorSerializableState>) {
        Timber.d("setPersistedCalculatorState $calculatorState")
        try {
            val json = serializer.toJson(CalculatorSerializableState.fromCalculatorState(calculatorState))
            if (!json.isNullOrEmpty()) {
                prefs.edit().putString(PERSIST_KEY_NAME, json).apply()
            }

        } catch (t: Throwable) {
            Timber.e(t,"setPersistedCalculatorState failed")
        }
    }

    fun getTicker(calculatorState: CalculatorState, exchangeState: ExchangeState): Ticker? {
        return getTicker(calculatorState, exchangeState.tickers)
    }

    fun getTicker(calculatorState: CalculatorState, tickers: Map<String, Ticker>): Ticker? {
        return tickers.get(calculatorState.targetTicker)
    }

    fun getBitcoinPrice(calculatorState: CalculatorState, exchangeState: ExchangeState): BigDecimal? {
        return getBitcoinPrice(calculatorState, exchangeState.tickers)
    }

    fun getBitcoinPrice(calculatorState: CalculatorState, tickerMap: Map<String, Ticker>): BigDecimal? {
        return if (calculatorState.convertToFiat) {
            calculatorState.source

        } else {
            val ticker = getTicker(calculatorState, tickerMap)
            if (ticker != null) {
                ExchangeUtils.convertToBitcoin(calculatorState.source, ticker)

            } else {
                null
            }
        }
    }

    fun getFiatPrice(ticker: Ticker, calculatorState: CalculatorState, tickers: Map<String, Ticker>): BigDecimal? {
        return getFiatPrice(ticker, calculatorState, ExchangeState(tickers))
    }

    fun getFiatPrice(ticker: Ticker, calculatorState: CalculatorState, exchangeState: ExchangeState): BigDecimal? {
        return if (calculatorState.convertToFiat) {
            ExchangeUtils.convertFromBitcoin(calculatorState.source, ticker)

        } else if (calculatorState.targetTicker != null) {
            if (calculatorState.targetTicker == ticker.code) {
                calculatorState.source

            } else {
                val bitcoinPrice = getBitcoinPrice(calculatorState, exchangeState)
                if (bitcoinPrice != null) {
                    ExchangeUtils.convertFromBitcoin(bitcoinPrice, ticker)

                } else {
                    null
                }
            }

        } else {
            null
        }
    }
}