package io.chthonic.price_converter.data.model

/**
 * Created by jhavatar on 3/31/2018.
 */
sealed class FiatCurrency(override val code: String, override val sign: String): Currency {
    companion object {
        val values: List<FiatCurrency> by lazy {
            FiatCurrency::class.nestedClasses.filter { it.objectInstance is FiatCurrency }.map { it.objectInstance as FiatCurrency }
        }
    }

    object Zar: FiatCurrency("ZAR","\u0052")
    object Myr: FiatCurrency("MYR", "\u0052\u004d")
    object Idr: FiatCurrency("IDR", "\u0052\u0070")
    object Ngn: FiatCurrency("NGN", "\u20a6")
}