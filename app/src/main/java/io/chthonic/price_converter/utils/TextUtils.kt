package io.chthonic.price_converter.utils

import android.content.res.Resources
import android.text.format.DateUtils
import com.github.salomonbrys.kodein.instance
import io.chthonic.price_converter.App
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.Currency
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by jhavatar on 4/2/2018.
 */
object TextUtils {

    val PLACE_HOLDER_STRING: String by lazy {
        App.kodein.instance<Resources>().getString(R.string.placeholder)
    }

    const val CRYPTO_DECIMAL_DIGITS = 8
    const val FIAT_DECIMAL_DIGITS = 2

    private val timeFormatter: SimpleDateFormat by lazy {
        SimpleDateFormat("HH:mm:ss")
    }

    private val dateFormatter: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd")
    }

    fun getDateTimeString(time: Long): String {
        val date = Date(time)
        return if (DateUtils.isToday(time)) {
            timeFormatter.format(date)

        } else {
            dateFormatter.format(date)
        }
    }

    val currencyFormatReplaceRegex: Regex by lazy {
        val chars: MutableList<String> = mutableListOf<String>(" ", """\${PLACE_HOLDER_STRING}""")

//        val cryptoChars: List<String> = CryptoCurrency.values.map{
//            it.sign.toCharArray().toList()
//        }.flatten().map { it.toString() }
//        chars.addAll(cryptoChars)
//
//        val fiatChars: List<String> = FiatCurrency.values.map{
//            it.sign.toCharArray().toList()
//        }.flatten().map { it.toString() }
//        chars.addAll(fiatChars)

        val s = chars.distinct().joinToString (separator = "", transform = {
            it
        })
        Timber.d("currencyFormatReplaceRegex: s = $s")

        """[$s]""".toRegex()
    }

    val fiatCurrencyFormat: DecimalFormat by lazy {
        val pattern = "###,##0.00";
        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
        formatSymbols.setDecimalSeparator('.')
        formatSymbols.setGroupingSeparator(' ')
        DecimalFormat(pattern, formatSymbols)
    }

    val cryptoCurrencyFormat: DecimalFormat by lazy {
        val pattern = "###,##0.00${"#".repeat(CRYPTO_DECIMAL_DIGITS - 2)}";
        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
        formatSymbols.setDecimalSeparator('.')
        formatSymbols.setGroupingSeparator(' ')
        DecimalFormat(pattern, formatSymbols)
    }

    fun formatCurrency(amount: BigDecimal?, fallback: String = PLACE_HOLDER_STRING, isCrypto: Boolean = false): String {
        return if (amount != null) {
            if (isCrypto) {
                cryptoCurrencyFormat.format(amount.setScale(CRYPTO_DECIMAL_DIGITS, RoundingMode.DOWN))
            } else {
                fiatCurrencyFormat.format(amount.setScale(FIAT_DECIMAL_DIGITS, RoundingMode.DOWN))
            }

        } else {
            fallback
        }
    }

    fun deFormatCurrency(s: String): String {
        return s.toString().replace(currencyFormatReplaceRegex, "")
    }

    fun getCurrencyVectorRes(currency: Currency): Int {
        return UiUtils.getCurrencyVectorRes(currency.code)
    }
}