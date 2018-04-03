package io.chthonic.bitcoin.calculator.data.model

/**
 * Created by jhavatar on 3/31/2018.
 */
sealed class FiatCurrency(override val code: String): Currency {
    companion object {
        val values: List<FiatCurrency> by lazy {
            FiatCurrency::class.nestedClasses.filter { it.objectInstance is FiatCurrency }.map { it.objectInstance as FiatCurrency }
        }
    }

    object Zar: FiatCurrency("ZAR")
    object Myr: FiatCurrency("MYR")
    object Idr: FiatCurrency("IDR")
    object Ngn: FiatCurrency("NGN")
}