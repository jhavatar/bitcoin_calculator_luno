package io.chthonic.bitcoin.calculator.ui.presenter

import android.os.Bundle
import android.os.Looper
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import io.chthonic.bitcoin.calculator.App
import io.chthonic.bitcoin.calculator.business.service.CalculatorService
import io.chthonic.bitcoin.calculator.business.service.ExchangeService
import io.chthonic.bitcoin.calculator.data.model.CalculatorState
import io.chthonic.bitcoin.calculator.data.model.Currency
import io.chthonic.bitcoin.calculator.data.model.ExchangeState
import io.chthonic.bitcoin.calculator.data.model.Ticker
import io.chthonic.bitcoin.calculator.ui.model.CalculationViewModel
import io.chthonic.bitcoin.calculator.ui.model.TickerViewModel
import io.chthonic.bitcoin.calculator.ui.vu.MainVu
import io.chthonic.bitcoin.calculator.utils.CalculatorUtils
import io.chthonic.bitcoin.calculator.utils.ExchangeUtils
import io.chthonic.bitcoin.calculator.utils.TextUtils
import io.chthonic.bitcoin.calculator.utils.UiUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.ThreadPoolDispatcher
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import timber.log.Timber


/**
 * Created by jhavatar on 3/28/2018.
 */
class MainPresenter(private val kodein: Kodein = App.kodein): BasePresenter<MainVu>() {

    private val exchangeService: ExchangeService by kodein.lazy.instance<ExchangeService>()
    private val calculatorService: CalculatorService by kodein.lazy.instance<CalculatorService>()

    private val clearDispatcher: ThreadPoolDispatcher by lazy {
        newSingleThreadContext("clear")
    }

    override fun onLink(vu: MainVu, inState: Bundle?, args: Bundle) {
        super.onLink(vu, inState, args)

        // force update all ui
        vu.updateCalculation(genCalculationViewModel(calculatorService.state).copy(forceSet = true))
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

        rxSubs.add(vu!!.leftInputObserver
                .observeOn(Schedulers.computation())
                .subscribe({amount: String ->
                    Timber.d("leftInputObserver success = $amount")
                    calculatorService.updateSourceDirectionAndSourceValue(true, amount)

                }, {

                    Timber.e(it, "leftInputObserver failed")
                }))

        rxSubs.add(vu!!.rightInputObserver
                .observeOn(Schedulers.computation())
                .subscribe({amount: String ->
                    Timber.d("rightInputObserver success = $amount")
                    calculatorService.updateSourceDirectionAndSourceValue(false, amount)

                }, {

                    Timber.e(it, "rightInputObserver failed")
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

    fun clearCalculation() {
        launch(clearDispatcher) {
//            Timber.d("clearCalculation: mainThread = ${Looper.myLooper() == Looper.getMainLooper()}")
            calculatorService.clear()
        }
    }


    private fun genCalculationViewModel(calculatorState: CalculatorState, exchangeState: ExchangeState = exchangeService.state): CalculationViewModel {
        Timber.d("genCalculationViewModel: mainThread = ${Looper.myLooper() == Looper.getMainLooper()}, calculatorState = $calculatorState")
        val leftTicker = CalculatorUtils.getLeftTicker(calculatorState, exchangeState)
        val leftTickerViewModel = if (leftTicker != null) {
            val leftCurrency = ExchangeUtils.getCurrencyForTicker(leftTicker)
            TickerViewModel(leftTicker.code, leftTicker.code,
                    TextUtils.formatCurrency(CalculatorUtils.getPrice(leftTicker, calculatorState, exchangeState), leftCurrency?.decimalDigits),
                    UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(leftTicker), ""),
                    leftCurrency?.decimalDigits ?: Currency.FIAT_DECIMAL_DIGITS,
                    true,
                    false,
                    calculatorState.leftTickerIsSource,
                    TextUtils.getDateTimeString(leftTicker.timestamp))
        } else {
            null
        }
        Timber.d("genCalculationViewModel: leftTicker = $leftTicker, \nleftTickerViewModel = $leftTickerViewModel")

        val rightTicker = CalculatorUtils.getRightTicker(calculatorState, exchangeState)
        val rightTickerViewModel = if (rightTicker != null) {
            val rightCurrency = ExchangeUtils.getCurrencyForTicker(rightTicker)
            TickerViewModel(rightTicker.code, rightTicker.code,
                    TextUtils.formatCurrency(CalculatorUtils.getPrice(rightTicker, calculatorState, exchangeState), rightCurrency?.decimalDigits),
                    UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(rightTicker), ""),
                    rightCurrency?.decimalDigits ?: Currency.FIAT_DECIMAL_DIGITS,
                    false,
                    true,
                    !calculatorState.leftTickerIsSource,
                    TextUtils.getDateTimeString(rightTicker.timestamp))
        } else {
            null
        }
        Timber.d("genCalculationViewModel: rightTicker = $rightTicker, \nrightTickerViewModel = $rightTickerViewModel")

//        Timber.d("genCalculationViewModel: currency = ${ExchangeUtils.getCurrencyForTicker(ticker!!)}")
        return CalculationViewModel(
                calculatorState.leftTickerIsSource,
                leftTicker = leftTickerViewModel,
                rightTicker = rightTickerViewModel,
                forceSet = (CalculatorState.getFactoryState() == calculatorState))
    }

    private fun genTickerViewModels(tickers: Map<String, Ticker>, calcState: CalculatorState = calculatorService.state): List<TickerViewModel> {
        Timber.d("genTickerViewModels: mainThread = ${Looper.myLooper() == Looper.getMainLooper()}, tickers = $tickers")
        val leftTicker = CalculatorUtils.getLeftTicker(calcState, tickers)
        val rightTicker = CalculatorUtils.getRightTicker(calcState, tickers)
        return tickers.values
                .filter{ ExchangeUtils.isSupportedCurrency(it) }
                .sortedBy { it.code }
                .map {
                    val currency = ExchangeUtils.getCurrencyForTicker(it)
                    val isLeft = leftTicker?.code == it.code
                    val isRight = rightTicker?.code == it.code
                    val isSource = (isLeft && calcState.leftTickerIsSource) || (isRight && !calcState.leftTickerIsSource)
                    TickerViewModel(it.code,
                            it.code,
                            TextUtils.formatCurrency(CalculatorUtils.getPrice(it, calculatorService.state, tickers), currency?.decimalDigits),
                            UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(it), ""),
                            currency?.decimalDigits ?: Currency.FIAT_DECIMAL_DIGITS,
                            isLeft,
                            isRight,
                            isSource,
                            TextUtils.getDateTimeString(it.timestamp))
                }
    }

}