package io.chthonic.price_converter.utils

import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.Ticker
import java.math.BigDecimal
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


    fun convertToFiat(amount: BigDecimal, ticker: Ticker): BigDecimal {
        return amount.multiply(ticker.bid.toBigDecimal())
    }

    fun convertToBitcoin(amount: BigDecimal, ticker: Ticker): BigDecimal {
        return amount.multiply(ticker.ask.toBigDecimal())
    }
}