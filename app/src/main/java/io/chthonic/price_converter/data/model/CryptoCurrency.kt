package io.chthonic.price_converter.data.model

/**
 * Created by jhavatar on 3/31/2018.
 */
sealed class CryptoCurrency(override val code: String, override val sign: String) : Currency {
    companion object {
        val values: List<CryptoCurrency> by lazy {
            CryptoCurrency::class.nestedClasses.filter { it.objectInstance is CryptoCurrency }.map { it.objectInstance as CryptoCurrency }
        }
    }

    object Bitcoin: CryptoCurrency("XBT", "\u20BF")
    object Ethereum: CryptoCurrency("ETH", "\u039E")
}