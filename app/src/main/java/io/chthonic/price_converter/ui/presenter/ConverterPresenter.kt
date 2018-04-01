package io.chthonic.price_converter.ui.presenter

import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.chthonic.price_converter.App
import io.chthonic.price_converter.business.service.CalculatorService
import io.chthonic.price_converter.business.service.ExchangeService
import io.chthonic.price_converter.data.model.CalculationViewModel
import io.chthonic.price_converter.data.model.CalculatorState
import io.chthonic.price_converter.data.model.Ticker
import io.chthonic.price_converter.data.model.TickerViewModel
import io.chthonic.price_converter.ui.vu.ConverterVu
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

        updateCalculation(calculatorService.state, true)
        updateTickers(exchangeService.state.tickers)
        Timber.d("ui init completed")

        subscribeServiceListeners()
        subscribeVuListeners()
        Timber.d("listeners subscribe completed")
    }

    fun subscribeServiceListeners() {
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

        rxSubs.add(calculatorService.observers.calculationChangeChangeObserver
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({calcState: CalculatorState ->
                    Timber.d("calculationChangeChangeObserver success = $calcState")
                    updateCalculation(calcState)
                    updateTickers(exchangeService.state.tickers)


                }, {it: Throwable ->
                    Timber.e(it, "fetchTickerLot fail")
                }))
    }

    fun subscribeVuListeners() {
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


    fun updateCalculation(calculatorState: CalculatorState, initPhase: Boolean = false) {
        val ticker = ExchangeUtils.getTicker(calculatorState, exchangeService.state)

        vu?.updateCalculation(CalculationViewModel(UiUtils.formatCurrency(ExchangeUtils.getBitcoinPrice(calculatorState, exchangeService.state),
                isCrypto = true),
                calculatorState.convertToFiat,
                if (ticker != null)  {
                    TickerViewModel(ticker.code, ticker.code,
                            UiUtils.formatCurrency(ExchangeUtils.getFiatPrice(ticker, calculatorState, exchangeService.state)),
                            ExchangeUtils.getFiatCurrencyForTicker(ticker)?.sign ?: "",
                            true)
                } else {
                    null
                }), initPhase)
    }

    fun updateTickers(tickers: Map<String, Ticker>) {
        Timber.d("updateTickers: tickers = $tickers")
        val targetTicker = ExchangeUtils.getTicker(calculatorService.state, tickers)
        val tickerList = tickers.values
                .filter{ ExchangeUtils.isSupportedFiatCurrency(it) }
                .map {
                    TickerViewModel(it.code,
                            it.code,
                            UiUtils.formatCurrency(ExchangeUtils.getFiatPrice(it, calculatorService.state, tickers)),
                            ExchangeUtils.getFiatCurrencyForTicker(it)?.sign ?: "",
                            targetTicker?.code == it.code)
                }
        Timber.d("updateTickers: tickerList = $tickerList")
        vu?.updateTickers(tickerList)
    }

}