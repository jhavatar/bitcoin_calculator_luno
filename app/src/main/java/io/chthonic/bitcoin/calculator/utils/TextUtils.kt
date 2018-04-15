package io.chthonic.bitcoin.calculator.utils

import android.text.format.DateUtils
import io.chthonic.bitcoin.calculator.data.model.CurrencyRealtimeFormatInput
import io.chthonic.bitcoin.calculator.data.model.CurrencyRealtimeFormatOutput
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
    const val PLACE_HOLDER_STRING: String = "\u2014"
    const val DECIMAL_SEPARATOR : Char = '.'
    const val DECIMAL_SEPARATOR_STRING = DECIMAL_SEPARATOR.toString()
    const val GROUPING_SEPARATOR: Char = ' '
    const val GROUPING_SEPARATOR_STRING = GROUPING_SEPARATOR.toString()
    const val CRYPTO_DECIMAL_DIGITS = 8
    const val FIAT_DECIMAL_DIGITS = 2
    const val TOO_MANY_DIGITS_MSG = "MAX"
    const val FALLBACK_CURRENCY_STRING = "0.00"

    internal val timeFormatter: SimpleDateFormat by lazy {
        SimpleDateFormat("HH:mm:ss")
    }

    internal val dateFormatter: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd")
    }

    private val currencyFormatReplaceRegex: Regex by lazy {
        """[$GROUPING_SEPARATOR_STRING$PLACE_HOLDER_STRING[a-zA-Z]]""".toRegex()
    }

    fun isCurrencyInWarningState(s: String?): Boolean {
        return (s == TextUtils.PLACE_HOLDER_STRING) || (s == TextUtils.TOO_MANY_DIGITS_MSG)
    }

    fun wasCurrencyWarningState(prevS: String?, delAction: Boolean): Boolean {
        return delAction && ((prevS == TextUtils.PLACE_HOLDER_STRING) || (prevS == TextUtils.TOO_MANY_DIGITS_MSG))
    }

    private val fiatCurrencyFormat: DecimalFormat by lazy {
        val pattern = "###,##0.00"
        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
        formatSymbols.setDecimalSeparator(DECIMAL_SEPARATOR)
        formatSymbols.setGroupingSeparator(GROUPING_SEPARATOR)
        DecimalFormat(pattern, formatSymbols)
    }

    private  val cryptoCurrencyFormat: DecimalFormat by lazy {
        val pattern = "###,##0.00${"#".repeat(CRYPTO_DECIMAL_DIGITS - 2)}"
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



    fun realtimeFormatInput(input: CurrencyRealtimeFormatInput): CurrencyRealtimeFormatOutput {
//        Timber.d("realtimeFormatInput: input = $input")

        if (TextUtils.isCurrencyInWarningState(input.s)) {
//            Timber.d("realtimeFormatInput: CurrencyInWarningState -> doNothing")
            return CurrencyRealtimeFormatOutput(doNothing = true)
        }

        val prevDecimalPos = input.sPrev.indexOf(DECIMAL_SEPARATOR)
        val caretIsDecimal = (prevDecimalPos >= 0) && (input.caretPos > input.sPrev.indexOf(DECIMAL_SEPARATOR))

        val sRawTemp = if (TextUtils.wasCurrencyWarningState(input.sPrev, input.delAction) || input.s.isEmpty()) {
            FALLBACK_CURRENCY_STRING

        } else {
            TextUtils.deFormatCurrency(input.s).let {
                if (it.isEmpty()) {
                    FALLBACK_CURRENCY_STRING
                } else {
                    it
                }
            }
        }
//        Timber.d("realtimeFormatInput: sRawTemp = $sRawTemp")

        val sFormattedTemp = TextUtils.formatCurrency(BigDecimal(sRawTemp), isCrypto = input.isCrypto)
        val revert = (sFormattedTemp.length >= input.maxLength) || (input.changed == DECIMAL_SEPARATOR_STRING) || (input.changed == GROUPING_SEPARATOR_STRING)
        val sFormatted = if (revert) {
            input.sPrev

        } else {
            sFormattedTemp
        }
        val sRaw = deFormatCurrency(sFormatted)
//        Timber.d("realtimeFormatInput:  sFormatted = $sFormatted, revert = $revert sFormattedTemp = $sFormattedTemp")

        val delta = Math.max(1, Math.abs(sFormatted.length - input.sPrev.length))
        val decimalPos = sFormatted.indexOf(DECIMAL_SEPARATOR)
//        Timber.d("realtimeFormatInput:  delta = $delta, decimalPos = $decimalPos")

        val caretPosTemp = if (revert && !input.delAction) {
           input.caretPos

        } else if (input.delAction) {
            input.caretPos - delta

        } else {
            input.caretPos + delta
        }

        // keep caret on same side of decimal
        val caretPos = if (caretIsDecimal) {
            Math.max(Math.min(sFormatted.length, caretPosTemp), if (input.delAction) 0 else decimalPos+1)
        } else {
            Math.max(Math.min(decimalPos, caretPosTemp), 0)
        }
//        Timber.d("realtimeFormatInput: caretPos = $caretPos, caretPosTemp = $caretPosTemp")

        return CurrencyRealtimeFormatOutput(sFormatted = sFormatted,
                sRaw = sRaw,
                caretPos = caretPos,
                doNothing = false,
                revert = revert)
    }
}