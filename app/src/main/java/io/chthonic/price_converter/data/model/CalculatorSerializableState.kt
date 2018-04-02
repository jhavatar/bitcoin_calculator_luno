package io.chthonic.price_converter.data.model

import java.math.BigDecimal

/**
 * Created by jhavatar on 4/2/2018.
 */
data class CalculatorSerializableState(val targetTicker: String?,
                                       val convertToFiat: Boolean,
                                       val source: String) {

    companion object {
        fun fromCalculatorState(state: CalculatorState): CalculatorSerializableState {
            return CalculatorSerializableState(state.targetTicker, state.convertToFiat, state.source.toString())
        }
    }

    fun toCalculatorState(): CalculatorState {
        return CalculatorState(targetTicker, convertToFiat, BigDecimal(source))
    }
}