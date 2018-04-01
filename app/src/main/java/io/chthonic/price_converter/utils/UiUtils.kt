package io.chthonic.price_converter.utils

import android.content.res.Resources
import android.view.View
import com.github.salomonbrys.kodein.instance
import io.chthonic.price_converter.App
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.CryptoCurrency
import io.chthonic.price_converter.data.model.Currency
import io.chthonic.price_converter.data.model.FiatCurrency
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by jhavatar on 3/30/2018.
 */
object UiUtils {

    val PLACE_HOLDER_STRING: String by lazy {
        App.kodein.instance<Resources>().getString(R.string.placeholder)
    }

    const val CRYPTO_DECIMAL_DIGITS = 8
    const val FIAT_DECIMAL_DIGITS = 2

    val currencyFormatReplaceRegex: Regex by lazy {
        val chars: MutableList<String> = mutableListOf<String>(" ", """\$PLACE_HOLDER_STRING""")

        val cryptoChars: List<String> = CryptoCurrency.values.map{
            it.sign.toCharArray().toList()
        }.flatten().map { it.toString() }
        chars.addAll(cryptoChars)

        val fiatChars: List<String> = FiatCurrency.values.map{
            it.sign.toCharArray().toList()
        }.flatten().map { it.toString() }
        chars.addAll(fiatChars)

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

    fun getCurrencyVectorRes(currency: Currency): Int {
        return getCurrencyVectorRes(currency.code)
    }

    fun getCurrencyVectorRes(code: String): Int {
        return when (code) {
            CryptoCurrency.Bitcoin.code -> R.drawable.ic_xbt
            FiatCurrency.Zar.code -> R.drawable.ic_zar
            FiatCurrency.Myr.code -> R.drawable.ic_myr
            FiatCurrency.Idr.code -> R.drawable.ic_idr
            FiatCurrency.Ngn.code -> R.drawable.ic_ngn
            else -> throw RuntimeException("code $code should not exist")
        }
    }

    fun getFiatImageSmallRes(code: String): Int {
        return when (code) {
            FiatCurrency.Zar.code -> R.drawable.ic_zar_320px
            FiatCurrency.Myr.code -> R.drawable.ic_myr_320px
            FiatCurrency.Idr.code -> R.drawable.ic_idr_320px
            FiatCurrency.Ngn.code -> R.drawable.ic_ngn_320px
            else -> throw RuntimeException("code $code should not exist")
        }
    }

    /**
     * note, view requires android:background="?android:attr/selectableItemBackground"
     */
    fun setRipple(view: View) {
        val attrs = intArrayOf(R.attr.selectableItemBackground)
        val typedArray = view.context.obtainStyledAttributes(attrs)
        val backgroundResource = typedArray.getResourceId(0, 0)
        view.setBackgroundResource(backgroundResource)
    }
}