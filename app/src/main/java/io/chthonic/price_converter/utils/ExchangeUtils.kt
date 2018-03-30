package io.chthonic.price_converter.utils

import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.CalculatorState
import io.chthonic.price_converter.data.model.ExchangeState
import io.chthonic.price_converter.data.model.Ticker
import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext
import java.text.DecimalFormat

/**
 * Created by jhavatar on 3/25/2018.
 */
object ExchangeUtils {

    sealed class FiatCurrency(val id: String, val displayNameRes: Int, val format: String) {
        object Zar: FiatCurrency("XBTZAR", R.string.zar_display_name, "\u0052%s")
        object Myr: FiatCurrency("XBTMYR", R.string.myr_display_name, "\u0052\u004d%s")
        object Idr: FiatCurrency("XBTIDR", R.string.idr_display_name, "\u0052\u0070%s")
        object Ngn: FiatCurrency("XBTNGN", R.string.ngn_display_name, "\u20a6%s")

        val code: String
            get() = id.substring(3)
    }

    private val idToFiatCurrencyMap: Map<String, FiatCurrency> by lazy {
        mapOf<String, FiatCurrency>(Pair(FiatCurrency.Zar.id, FiatCurrency.Zar),
                Pair(FiatCurrency.Myr.id, FiatCurrency.Myr),
                Pair(FiatCurrency.Idr.id, FiatCurrency.Idr),
                Pair(FiatCurrency.Ngn.id, FiatCurrency.Ngn))
    }

    fun getFiatCurrencyForTicker(ticker: Ticker): FiatCurrency? {
        return idToFiatCurrencyMap[ticker.pair]
    }

    fun isSupportedFiatCurrency(ticker: Ticker): Boolean {
        return getFiatCurrencyForTicker(ticker) != null
    }


    fun formatCurrency(ticker: Ticker, amount: BigDecimal, fallback: String): String {
        val pattern = "###,###.###";
        val decimalFormat = DecimalFormat(pattern)
        val fiat = getFiatCurrencyForTicker(ticker)
        return if (fiat != null) {
            String.format(fiat.format, decimalFormat.format(amount))

        } else {
            fallback
        }
    }

    fun getTicker(calculatorState: CalculatorState, exchangeState: ExchangeState): Ticker? {
        return exchangeState.tickers.get(calculatorState.targetTicker)
    }

    fun convertToFiat(bitcoinPrice: BigDecimal, ticker: Ticker): BigDecimal {
        return bitcoinPrice.multiply(ticker.bid.toBigDecimal())
    }

    fun convertToBitcoin(fiatPrice: BigDecimal, ticker: Ticker): BigDecimal {
        Timber.d("convertToBitcoin: fiatPrice = $fiatPrice, ticker = $ticker")
        return fiatPrice.divide(ticker.ask.toBigDecimal(), MathContext.DECIMAL64)
    }

    fun getBitcoinPrice(calculatorState: CalculatorState, exchangeState: ExchangeState): BigDecimal? {
        return if (calculatorState.convertToFiat) {
            calculatorState.source

        } else {
            val ticker = getTicker(calculatorState, exchangeState)
            if (ticker != null) {
                convertToBitcoin(calculatorState.source, ticker)

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
            convertToFiat(calculatorState.source, ticker)

        } else if (calculatorState.targetTicker == ticker.pair) {
            val bitcoinPrice = convertToBitcoin(calculatorState.source, ticker)
            calculatorState.source

        } else if (calculatorState.targetTicker != null) {
            val bitcoinPrice = if (calculatorState.targetTicker == ticker.pair) {
                convertToBitcoin(calculatorState.source, ticker)

            } else {
                getBitcoinPrice(calculatorState, exchangeState)
            }

            if (bitcoinPrice != null) {
                convertToFiat(bitcoinPrice, ticker)

            } else {
                null
            }

        } else {
            null
        }
    }

}