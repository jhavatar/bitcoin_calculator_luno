package io.chthonic.price_converter.business.actions

import com.yheriatovych.reductor.Action
import com.yheriatovych.reductor.annotations.ActionCreator
import java.math.BigDecimal

/**
 * Created by jhavatar on 3/30/2018.
 */
@ActionCreator
interface CalculatorActions {
    companion object {
        const val SET_TARGET_TICKER: String = "SET_TARGET_TICKER"
        const val SWITCH_CONVERT_DIRECTION: String = "SWITCH_CONVERT_DIRECTION"
        const val UPDATE_SOURCE: String = "UPDATE_SOURCE"
        const val SWITCH_CONVERT_DIRECTION_AND_UPDATE_SOURCE: String = "SWITCH_CONVERT_DIRECTION_AND_UPDATE_SOURCE"
        const val CLEAR: String = "CLEAR"
    }

    @ActionCreator.Action(SET_TARGET_TICKER)
    fun setTargetTicker(tickerCode: String): Action

    @ActionCreator.Action(SWITCH_CONVERT_DIRECTION)
    fun switchConvertDirection(convertToFiat: Boolean): Action

    @ActionCreator.Action(UPDATE_SOURCE)
    fun updateSource(source: BigDecimal): Action

    @ActionCreator.Action(SWITCH_CONVERT_DIRECTION_AND_UPDATE_SOURCE)
    fun switchConvertDirectionAndUpdateSource(convertToFiat: Boolean, source: BigDecimal): Action

    @ActionCreator.Action(CLEAR)
    fun clear(): Action
}