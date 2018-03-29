package io.chthonic.price_converter.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.chthonic.price_converter.data.model.TickerViewModel
import io.chthonic.price_converter.ui.viewholder.TickerHolder

/**
 * Created by jhavatar on 3/28/2018.
 */
class TickerListAdapter: RecyclerView.Adapter<TickerHolder>() {

    var _items: List<TickerViewModel> = emptyList()
    var items: List<TickerViewModel>
        get() = _items
        set(nuItems: List<TickerViewModel>) {
            _items = nuItems.toList()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TickerHolder {
        return TickerHolder(TickerHolder.createView(parent))
    }

    override fun getItemCount(): Int {
        return _items.size
    }

    override fun onBindViewHolder(holder: TickerHolder, position: Int) {
        holder.update(items[position])
    }
}