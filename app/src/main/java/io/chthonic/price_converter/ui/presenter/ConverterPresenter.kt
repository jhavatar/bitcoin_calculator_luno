package io.chthonic.price_converter.ui.presenter

import android.os.Bundle
import android.os.Looper
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.chthonic.price_converter.App
import io.chthonic.price_converter.business.service.CalculatorService
import io.chthonic.price_converter.business.service.ExchangeService
import io.chthonic.price_converter.data.model.*
import io.chthonic.price_converter.ui.model.CalculationViewModel
import io.chthonic.price_converter.ui.model.TickerViewModel
import io.chthonic.price_converter.ui.vu.ConverterVu
import io.chthonic.price_converter.utils.CalculatorUtils
import io.chthonic.price_converter.utils.ExchangeUtils
import io.chthonic.price_converter.utils.UiUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by jhavatar on 3/28/2018.
 */
class ConverterPresenter(private val kodein: Kodein = App.kodein): BasePresenter<ConverterVu>() {

    private val exchangeService: ExchangeService by kodein.lazy.instance<ExchangeService>()
    private val calculatorService: CalculatorService by kodein.lazy.instance<CalculatorService>()

    override fun onLink(vu: ConverterVu, inState: Bundle?, args: Bundle) {
        super.onLink(vu, inState, args)

        vu.updateCalculation(genCalculationViewModel(calculatorService.state), true)
        vu.updateTickers(genTickerViewModels(exchangeService.state.tickers))
        Timber.d("ui init completed")

        subscribeServiceListeners()
        subscribeVuListeners()
        Timber.d("listeners subscribe completed")

        fetchLatestTickers()
    }

    private fun subscribeServiceListeners() {
        rxSubs.add(exchangeService.observers.tickersChangeObserver
                .subscribeOn(Schedulers.computation())
                .map {tickerMap: Map<String, Ticker> ->
                    genTickerViewModels(tickerMap)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({tickers: List<TickerViewModel> ->
                    Timber.d("tickersChangeObserver success = $tickers")
                    vu?.updateTickers(tickers)

                }, {it: Throwable ->
                    Timber.e(it, "tickersChangeObserver fail")
                }))

        rxSubs.add(calculatorService.observers.calculationChangeChangeObserver
                .subscribeOn(Schedulers.computation())
                .map{ calcState: CalculatorState ->
                    val exchangeState = exchangeService.state
                    Pair<CalculationViewModel, List<TickerViewModel>>(genCalculationViewModel(calcState, exchangeState),
                            genTickerViewModels(exchangeState.tickers, calcState))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({viewModels: Pair<CalculationViewModel, List<TickerViewModel>> ->
                    Timber.d("calculationChangeChangeObserver success = $viewModels")
                    vu?.updateCalculation(viewModels.first)
                    vu?.updateTickers(viewModels.second)

                }, {it: Throwable ->
                    Timber.e(it, "fetchTickerLot fail")
                }))
    }


    private fun subscribeVuListeners() {
        rxSubs.add(vu!!.tickerSelectObservable
                .observeOn(Schedulers.computation())
                .subscribe({ tickerCode: String ->
                    Timber.d("tickerSelectObservable success: $tickerCode")
                    calculatorService.setTargetTicker(tickerCode)

                }, {t: Throwable ->
                    Timber.e(t, "tickerSelectObservable failed")
                }))

        rxSubs.add(vu!!.bitcoinInputObserver
                .observeOn(Schedulers.computation())
                .subscribe({bitcoinAmount: String ->
                    Timber.d("bitcoinInputObserver success = $bitcoinAmount")
                    calculatorService.switchConvertDirectionAndUpdateSource(true, bitcoinAmount)

                }, {

                    Timber.e(it, "bitcoinInputObserver failed")
                }))

        rxSubs.add(vu!!.fiatInputObserver
                .observeOn(Schedulers.computation())
                .subscribe({fiatAmount: String ->
                    Timber.d("fiatInputObserver success = $fiatAmount")
                    calculatorService.switchConvertDirectionAndUpdateSource(false, fiatAmount)

                }, {

                    Timber.e(it, "fiatInputObserver failed")
                }))
    }

    fun fetchLatestTickers(forceDisplayLoading: Boolean = false) {
        Timber.d("fetchLatestTickers: forceDisplayLoading = $forceDisplayLoading")
        if (forceDisplayLoading || exchangeService.state.tickers.isEmpty()) {
            vu?.showLoading()
        }

        rxSubs.add(exchangeService.fetchTickerLot()
//                .toCompletable()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({it: List<Ticker> ->
                    Timber.d("fetchTickerLot success = $it")
                    vu?.hideLoading()

                }, {it: Throwable ->
                    Timber.e(it, "fetchTickerLot fail")
                    vu?.hideLoading()
                }))
    }


    private fun genCalculationViewModel(calculatorState: CalculatorState, exchangeState: ExchangeState = exchangeService.state): CalculationViewModel {
        Timber.d("genCalculationViewModel: mainThread = ${Looper.myLooper() == Looper.getMainLooper()}, calculatorState = $calculatorState")
        val ticker = CalculatorUtils.getTicker(calculatorState, exchangeState)
        return CalculationViewModel(UiUtils.formatCurrency(CalculatorUtils.getBitcoinPrice(calculatorState, exchangeState),
                isCrypto = true),
                calculatorState.convertToFiat,
                if (ticker != null) {
                    TickerViewModel(ticker.code, ticker.code,
                            UiUtils.formatCurrency(CalculatorUtils.getFiatPrice(ticker, calculatorState, exchangeState)),
                            ExchangeUtils.getFiatCurrencyForTicker(ticker)?.sign ?: "",
                            true,
                            UiUtils.getDateTimeString(ticker.timestamp))
                } else {
                    null
                })
    }

    private fun genTickerViewModels(tickers: Map<String, Ticker>, calcState: CalculatorState = calculatorService.state): List<TickerViewModel> {
        Timber.d("genTickerViewModels: mainThread = ${Looper.myLooper() == Looper.getMainLooper()}, tickers = $tickers")
        val targetTicker = CalculatorUtils.getTicker(calcState, tickers)
        return tickers.values
                .filter{ ExchangeUtils.isSupportedFiatCurrency(it) }
                .sortedBy { it.code }
                .map {
                    TickerViewModel(it.code,
                            it.code,
                            UiUtils.formatCurrency(CalculatorUtils.getFiatPrice(it, calculatorService.state, tickers)),
                            ExchangeUtils.getFiatCurrencyForTicker(it)?.sign ?: "",
                            targetTicker?.code == it.code,
                            UiUtils.getDateTimeString(it.timestamp))
                }
    }

}