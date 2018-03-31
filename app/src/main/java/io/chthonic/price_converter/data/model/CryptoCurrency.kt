package io.chthonic.price_converter.data.model

import timber.log.Timber

/**
 * Created by jhavatar on 3/31/2018.
 */
sealed class CryptoCurrency(val code: String, val sign: String) {
    companion object {
        val values: List<CryptoCurrency> by lazy {
//            Timber.d("CryptoCurrency nested classes = ${CryptoCurrency::class.nestedClasses}")
//            Timber.d("CryptoCurrency nested classes filtered = ${CryptoCurrency::class.nestedClasses.filter { it.objectInstance is CryptoCurrency }}")
            CryptoCurrency::class.nestedClasses.filter { it.objectInstance is CryptoCurrency }.map { it.objectInstance as CryptoCurrency }
        }
    }

    object Bitcoin: CryptoCurrency("XBT", "\u20BF")
    object Ethereum: CryptoCurrency("ETH", "\u039E")
}