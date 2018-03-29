package io.chthonic.price_converter.ui.vu

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.ViewGroup
import io.chthonic.mythos.mvp.FragmentWrapper
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.TickerViewModel
import io.chthonic.price_converter.ui.adapter.TickerListAdapter
import kotlinx.android.synthetic.main.vu_converter.view.*

/**
 * Created by jhavatar on 3/28/2018.
 */
class ConverterVu(inflater: LayoutInflater,
                  activity: Activity,
                  fragmentWrapper: FragmentWrapper? = null,
                  parentView: ViewGroup? = null) : BaseVu(inflater,
        activity = activity,
        fragmentWrapper = fragmentWrapper,
        parentView = parentView) {

    val toolbar: Toolbar
        get() = rootView.toolbar

    val listView: RecyclerView
        get() = rootView.list_tickers

    private lateinit var tickerAdapter: TickerListAdapter

    override fun getRootViewLayoutId(): Int {
        return R.layout.vu_converter
    }

    override fun onCreate() {
        super.onCreate()

        tickerAdapter = TickerListAdapter()
        listView.adapter = tickerAdapter
        listView.layoutManager = LinearLayoutManager(activity)
    }


    fun updateTickers(tickers: List<TickerViewModel>) {
        tickerAdapter.items = tickers
        tickerAdapter.notifyDataSetChanged()
    }
}