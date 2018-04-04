package io.chthonic.bitcoin.calculator.utils

import android.text.format.DateUtils
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
    const val PLACE_HOLDER_STRING: String = "\u2014"
    const val CRYPTO_DECIMAL_DIGITS = 8
    const val FIAT_DECIMAL_DIGITS = 2
    const val TOO_MANY_DIGITS_MSG = "MAX"

    internal val timeFormatter: SimpleDateFormat by lazy {
        SimpleDateFormat("HH:mm:ss")
    }

    internal val dateFormatter: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd")
    }

    private val currencyFormatReplaceRegex: Regex by lazy {
        """[ $PLACE_HOLDER_STRING[a-zA-Z]]""".toRegex()
    }

    fun isCurrencyInWarningState(s: String?): Boolean {
        return (s == TextUtils.PLACE_HOLDER_STRING) || (s == TextUtils.TOO_MANY_DIGITS_MSG)
    }

    fun wasCurrencyWarningState(prevS: String?, delAction: Boolean): Boolean {
        return delAction && ((prevS == TextUtils.PLACE_HOLDER_STRING) || (prevS == TextUtils.TOO_MANY_DIGITS_MSG))
    }

    private val fiatCurrencyFormat: DecimalFormat by lazy {
        val pattern = "###,##0.00";
        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
        formatSymbols.setDecimalSeparator('.')
        formatSymbols.setGroupingSeparator(' ')
        DecimalFormat(pattern, formatSymbols)
    }

    private  val cryptoCurrencyFormat: DecimalFormat by lazy {
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
//        Timber.d("deFormatCurrency: s = $s, result = ${s.replace(currencyFormatReplaceRegex, "")}")
        return s.replace(currencyFormatReplaceRegex, "")
    }

    fun getDateTimeString(time: Long): String {
        val date = Date(time)
        return if (DateUtils.isToday(time)) {
            timeFormatter.format(date)

        } else {
            dateFormatter.format(date)
        }
    }
}