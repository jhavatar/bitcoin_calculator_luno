package io.chthonic.price_converter.data.model

import java.math.BigDecimal

/**
 * Created by jhavatar on 3/30/2018.
 */
data class CalculatorState(val targetTicker: String?,
                      val convertToFiat: Boolean,
                      val source: BigDecimal) {
}