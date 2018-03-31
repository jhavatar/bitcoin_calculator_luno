package io.chthonic.price_converter.ui.viewholder

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
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

        val price = "<b>${ticker.sign}</b> ${ticker.price}"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            itemView.ticker_price.text = Html.fromHtml(price,  Html.FROM_HTML_MODE_COMPACT)
        } else {
            itemView.ticker_price.text = Html.fromHtml(price)
        }
    }

}