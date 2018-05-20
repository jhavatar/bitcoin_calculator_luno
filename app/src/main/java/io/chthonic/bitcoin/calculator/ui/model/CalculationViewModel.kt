package io.chthonic.bitcoin.calculator.ui.model

/**
 * Created by jhavatar on 3/30/2018.
 */
data class CalculationViewModel(val bitcoinPrice: String,
                                val convertFromBitcoin: Boolean,
                                val ticker: TickerViewModel?,
                                val forceSet: Boolean) {
}