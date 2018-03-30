package io.chthonic.price_converter.ui.vu

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import com.jakewharton.rxbinding2.widget.RxTextView
import hu.akarnokd.rxjava2.operators.FlowableTransformers
import io.chthonic.mythos.mvp.FragmentWrapper
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.CalculationViewModel
import io.chthonic.price_converter.data.model.TickerViewModel
import io.chthonic.price_converter.ui.adapter.TickerListAdapter
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject
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

    private val tickerSelectPublisher: PublishSubject<String> by lazy {
        PublishSubject.create<String>()
    }
    val tickerSelectObservable: Observable<String>
        get() = tickerSelectPublisher.hide()

    private val bitcoinInputValve: PublishProcessor<Boolean> = PublishProcessor.create()
    val bitcoinInputObserver: Flowable<String> by lazy {
        RxTextView.textChanges(bitcoinInput)
                .skipInitialValue() // ignore initial value sent without change
                .toFlowable(BackpressureStrategy.LATEST)
                .compose(FlowableTransformers.valve(bitcoinInputValve, true))
                .filter{
                    !it.isNullOrEmpty()
                }
                .map{
                    it.toString()
                }
    }

    private val fiatInputValve: PublishProcessor<Boolean> = PublishProcessor.create()
    val fiatInputObserver: Flowable<String> by lazy {
        RxTextView.textChanges(fiatInput)
                .skipInitialValue() // ignore initial value sent without change
                .toFlowable(BackpressureStrategy.LATEST)
                .compose(FlowableTransformers.valve(fiatInputValve, true))
                .filter{
                    !it.isNullOrEmpty()
                }
                .map{
                    it.toString()
                }
    }

    private val convertToFiatValve: PublishProcessor<Boolean> = PublishProcessor.create()
    val convertToFiatObserver: Flowable<Boolean> by lazy {
        RxCompoundButton.checkedChanges(conversionSwitch)
                .skipInitialValue()
                .toFlowable(BackpressureStrategy.LATEST)
                .compose(FlowableTransformers.valve(convertToFiatValve, true))
                .map{
                    !it
                }
    }

    val toolbar: Toolbar by lazy {
        rootView.toolbar
    }

    private val listView: RecyclerView by lazy {
        rootView.list_tickers
    }

    private val bitcoinInput: EditText by lazy {
        rootView.input_btx
    }

    private val fiatInput: EditText by lazy {
        rootView.input_fiat
    }

    private val conversionSwitch: SwitchCompat
        get() = rootView.switch_convert_from_fiat

    private lateinit var tickerAdapter: TickerListAdapter

    override fun getRootViewLayoutId(): Int {
        return R.layout.vu_converter
    }

    override fun onCreate() {
        super.onCreate()

        tickerAdapter = TickerListAdapter(tickerSelectPublisher)
        listView.adapter = tickerAdapter
        listView.layoutManager = LinearLayoutManager(activity)
    }


    fun updateCalculation(calc: CalculationViewModel, initPhase: Boolean = false) {
        val convertFromFiat = !calc.convertToFiat
        if (conversionSwitch.isChecked != convertFromFiat) {
            convertToFiatValve.onNext(false)
            conversionSwitch.isChecked = convertFromFiat
            convertToFiatValve.onNext(true)
        }

        if (initPhase || calc.convertToFiat) {
            fiatInputValve.onNext(false)
            fiatInput.setText(calc.ticker?.price ?: "null")
            fiatInputValve.onNext(true)
        }

        if (initPhase || !calc.convertToFiat) {
            bitcoinInputValve.onNext(false)
            bitcoinInput.setText(calc.bitcoinPrice)
            bitcoinInputValve.onNext(true)
        }
    }


    fun updateTickers(tickers: List<TickerViewModel>) {
        tickerAdapter.items = tickers
        tickerAdapter.notifyDataSetChanged()
    }
}