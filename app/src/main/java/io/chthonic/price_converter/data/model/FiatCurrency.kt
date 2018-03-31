package io.chthonic.price_converter.data.model

import io.chthonic.price_converter.R
import timber.log.Timber

/**
 * Created by jhavatar on 3/31/2018.
 */
sealed class FiatCurrency(val code: String, val sign: String) {
    companion object {
        val values: List<FiatCurrency> by lazy {
            Timber.d("FiatCurrency nested classes = ${FiatCurrency::class.nestedClasses}")
            FiatCurrency::class.nestedClasses.filter { it.objectInstance is FiatCurrency }.map { it.objectInstance as FiatCurrency }
        }
    }

    object Zar: FiatCurrency("ZAR","\u0052")
    object Myr: FiatCurrency("MYR", "\u0052\u004d")
    object Idr: FiatCurrency("IDR", "\u0052\u0070")
    object Ngn: FiatCurrency("NGN", "\u20a6")
}