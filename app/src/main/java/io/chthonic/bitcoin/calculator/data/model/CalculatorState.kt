package io.chthonic.bitcoin.calculator.data.model

import java.math.BigDecimal

/**
 * Created by jhavatar on 3/30/2018.
 */
data class CalculatorState(val leftTickerCode: String,
                           val rightTickerCode: String?,
                           val leftTickerIsSource: Boolean,
                           val sourceValue: BigDecimal
                           ) {
    companion object {
        fun getFactoryState(): CalculatorState {
            return CalculatorState(CryptoCurrency.Bitcoin.code, null,true, 1.toBigDecimal())
        }
    }

    val targetTickerCode: String?
        get() = if (leftTickerIsSource) rightTickerCode else leftTickerCode

    val sourceTickerCode: String
        get() = if (leftTickerIsSource) leftTickerCode else rightTickerCode!! // there will always be source
}