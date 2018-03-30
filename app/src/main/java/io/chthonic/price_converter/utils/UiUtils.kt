package io.chthonic.price_converter.utils

import android.content.Context
import android.content.res.Resources
import com.github.salomonbrys.kodein.instance
import io.chthonic.price_converter.App
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.Ticker
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by jhavatar on 3/30/2018.
 */
object UiUtils {

    val placeHolderString: String by lazy {
        App.kodein.instance<Resources>().getString(R.string.placeholder)
    }

    val currencyFormat: DecimalFormat by lazy {
        val pattern = "###,##0.00";
        val formatSymbols = DecimalFormatSymbols(Locale.ENGLISH)
        formatSymbols.setDecimalSeparator('.')
        formatSymbols.setGroupingSeparator(' ')
        DecimalFormat(pattern, formatSymbols)
    }

    fun formatCurrency(amount: BigDecimal?, fallback: String): String {
        return if (amount != null) {
            currencyFormat.format(amount)

        } else {
            fallback
        }
    }

    fun formatCurrency(amount: BigDecimal?): String {
        return formatCurrency(amount, placeHolderString)
    }
}