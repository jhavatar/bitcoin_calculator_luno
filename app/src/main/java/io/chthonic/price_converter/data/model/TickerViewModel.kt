package io.chthonic.price_converter.data.model

/**
 * Created by jhavatar on 3/29/2018.
 */
data class TickerViewModel(val code: String,
                           val name: String,
                           val price: String,
                           val sign: String,
                           val selected: Boolean,
                           val dateTime: String) {
}