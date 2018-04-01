package io.chthonic.price_converter.utils

import io.chthonic.price_converter.data.model.*
import timber.log.Timber
import java.math.BigDecimal
import java.math.MathContext

/**
 * Created by jhavatar on 3/25/2018.
 */
object ExchangeUtils {

    private val codeToFiatCurrencyMap: Map<String, FiatCurrency> by lazy {
        FiatCurrency.values.associateBy( {it.code}, {it} )
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


//    fun formatCurrency(ticker: Ticker, amount: BigDecimal, fallback: String): String {
//        val pattern = "###,###.###";
//        val decimalFormat = DecimalFormat(pattern)
//        val fiat = getFiatCurrencyForTicker(ticker)
//        return if (fiat != null) {
//            String.format(fiat.format, decimalFormat.format(amount))
//
//        } else {
//            fallback
//        }
//    }

    fun getTicker(calculatorState: CalculatorState, exchangeState: ExchangeState): Ticker? {
        return getTicker(calculatorState, exchangeState.tickers)
    }

    fun getTicker(calculatorState: CalculatorState, tickers: Map<String, Ticker>): Ticker? {
        return tickers.get(calculatorState.targetTicker)
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