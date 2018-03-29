package io.chthonic.price_converter.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.TickerViewModel
import kotlinx.android.synthetic.main.holder_ticker.view.*

/**
 * Created by jhavatar on 3/28/2018.
 */
class TickerHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    companion object {
        fun createView(parent: ViewGroup): View {
            return LayoutInflater.from(parent.context).inflate(R.layout.holder_ticker, parent, false)
        }
    }

    fun update(ticker: TickerViewModel) {
        itemView.ticker_name.text = ticker.name
        itemView.ticker_price.text = ticker.price
    }

}