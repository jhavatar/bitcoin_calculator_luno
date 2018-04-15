package io.chthonic.bitcoin.calculator.data.model

/**
 * Created by jhavatar on 4/14/2018.
 */
data class CurrencyRealtimeFormatInput(val s: String,
                                       val sPrev: String,
                                       val changed: String,
                                       val delAction: Boolean,
                                       val caretPos: Int,
                                       val maxLength: Int,
                                       val isCrypto: Boolean) {

    companion object {

        fun factoryReset(): CurrencyRealtimeFormatInput {
            return CurrencyRealtimeFormatInput(s = "",
                    sPrev = "",
                    changed = "",
                    delAction = false,
                    caretPos = 0,
                    maxLength = Int.MAX_VALUE,
                    isCrypto = false
                    )
        }
    }
}