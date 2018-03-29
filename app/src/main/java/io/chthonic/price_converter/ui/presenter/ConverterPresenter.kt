package io.chthonic.price_converter.ui.presenter

import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.chthonic.price_converter.App
import io.chthonic.price_converter.business.service.ExchangeService
import io.chthonic.price_converter.data.model.Ticker
import io.chthonic.price_converter.data.model.TickerViewModel
import io.chthonic.price_converter.ui.vu.ConverterVu
import io.chthonic.price_converter.ui.vu.MainVu
import io.chthonic.price_converter.utils.ExchangeUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by jhavatar on 3/28/2018.
 */
class ConverterPresenter(val kodein: Kodein = App.kodein): BasePresenter<ConverterVu>() {

    val exchangeService: ExchangeService by kodein.lazy.instance<ExchangeService>()

    override fun onLink(vu: ConverterVu, inState: Bundle?, args: Bundle) {
        super.onLink(vu, inState, args)

        updateTickers(exchangeService.state.tickers)
        rxSubs.add(exchangeService.observers.tickersChangeObserver
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({it: Map<String, Ticker> ->
                    Timber.d("tickersChangeObserver success = $it")
                    updateTickers(it)

                }, {it: Throwable ->
                    Timber.e(it, "tickersChangeObserver fail")
                }))

        rxSubs.add(exchangeService.fetchTickerLot()
//                .toCompletable()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe({it: List<Ticker> ->
                    Timber.d("fetchTickerLot success = $it")

                }, {it: Throwable ->
                    Timber.e(it, "fetchTickerLot fail")
                }))
    }

    fun updateTickers(tickers: Map<String, Ticker>) {
        Timber.d("updateTickers: tickers = $tickers")
        val tickerList = tickers.values
                .filter{ ExchangeUtils.isSupportedFiatCurrency(it) }
                .map {
                    TickerViewModel(it.pair, ExchangeUtils.getFiatCurrencyForTicker(it)?.code!!, "R100")
                }
        Timber.d("updateTickers: tickerList = $tickerList")
        vu?.updateTickers(tickerList)
    }

}