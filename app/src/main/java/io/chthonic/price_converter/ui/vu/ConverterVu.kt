package io.chthonic.price_converter.ui.vu

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import io.chthonic.mythos.mvp.FragmentWrapper
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.CalculationViewModel
import io.chthonic.price_converter.data.model.TickerViewModel
import io.chthonic.price_converter.ui.adapter.TickerListAdapter
import io.chthonic.price_converter.utils.UiUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.vu_converter.view.*
import timber.log.Timber
import java.math.BigDecimal

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

    //    private val bitcoinInputValve: PublishProcessor<Boolean> = PublishProcessor.create()
//    private var pauseBitcoinInputObservable: Boolean = false
//    val bitcoinInputObserver: Flowable<String> by lazy {
//        RxTextView.textChanges(bitcoinInput)
//                .skipInitialValue() // ignore initial value sent without change
//                .toFlowable(BackpressureStrategy.LATEST)
////                .compose(FlowableTransformers.valve(bitcoinInputValve, true))
//                .filter{
//                    !it.isNullOrEmpty() && !pauseBitcoinInputObservable
//                }
//                .map{
//                    it.toString()
//                }
//    }

    private val bitcoinInputPublisher: PublishSubject<String> by lazy {
        PublishSubject.create<String>()
    }
    val bitcoinInputObserver: Flowable<String>
        get() = bitcoinInputPublisher
                .hide()
                .toFlowable(BackpressureStrategy.LATEST)

    private val bitcoinInputWatcher by lazy {
        object : TextWatcher {
            var prevString = ""
            var delAction = false
            var caretPos = 0

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == UiUtils.PLACE_HOLDER_STRING) {
                    return
                }

                bitcoinInput.removeTextChangedListener(this)

                val sRaw = s.toString().replace(UiUtils.currencyFormatReplaceRegex, "")
                val sFormatted = UiUtils.formatCurrency(BigDecimal(sRaw), isCrypto = true)
                bitcoinInput.setText(sFormatted)

//                Timber.d("bitcoinInputWatcher: selection start = ${bitcoinInput.selectionStart}, end = ${bitcoinInput.selectionEnd}, formatLength = ${sFormatted.length}")
                Timber.d("bitcoinInputWatcher: delAction = $delAction, caretPos = $caretPos")
                if (delAction) {
                    bitcoinInput.setSelection(Math.max(Math.min(sFormatted.length, caretPos - 1), 0))

                } else {
                    bitcoinInput.setSelection(Math.min(sFormatted.length, caretPos + 1))
                }
                prevString = sFormatted

                bitcoinInput.addTextChangedListener(this)
                bitcoinInputPublisher.onNext(sRaw)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                caretPos = bitcoinInput.selectionStart
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                delAction = s?.length ?: 0 < prevString.length
            }
        }
    }



    private val fiatInputPublisher: PublishSubject<String> by lazy {
        PublishSubject.create<String>()
    }
    val fiatInputObserver: Flowable<String>
        get() = fiatInputPublisher
                .hide()
                .toFlowable(BackpressureStrategy.LATEST)

    private val fiatInputWatcher by lazy {
        object:TextWatcher {
            var prevString = ""
            var delAction = false
            var caretPos = 0

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == UiUtils.PLACE_HOLDER_STRING) {
                    return
                }

                fiatInput.removeTextChangedListener(this)

                val sRaw = s.toString().replace(UiUtils.currencyFormatReplaceRegex, "")
                val sFormatted = UiUtils.formatCurrency(BigDecimal(sRaw))
                fiatInput.setText(sFormatted)

//                Timber.d("fiatInputWatcher: selection start = ${fiatInput.selectionStart}, end = ${fiatInput.selectionEnd}, formatLength = ${sFormatted.length}")
                Timber.d("fiatInputWatcher: delAction = $delAction, caretPos = $caretPos")
                if (delAction) {
                    fiatInput.setSelection(Math.max(Math.min(sFormatted.length, caretPos - 1), 0))

                } else {
                    fiatInput.setSelection(Math.min(sFormatted.length, caretPos + 1))
                }
                prevString = sFormatted

                fiatInput.addTextChangedListener(this)
                fiatInputPublisher.onNext(sRaw)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                caretPos = fiatInput.selectionStart
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                delAction = s?.length ?: 0 < prevString.length
            }
        }
    }


//    //    private val fiatInputValve: PublishProcessor<Boolean> = PublishProcessor.create()
//    private var pauseFiatInputObservable: Boolean = true
//    val fiatInputObserver: Flowable<String> by lazy {
//        RxTextView.textChanges(fiatInput)
//                .skipInitialValue() // ignore initial value sent without change
//                .toFlowable(BackpressureStrategy.LATEST)
////                .compose(FlowableTransformers.valve(fiatInputValve, true))
//                .filter{
//                    !it.isNullOrEmpty() && !pauseFiatInputObservable
//                }
//                .map{
//                    it.toString()
//                }
//    }

    //    private val convertToFiatValve: PublishProcessor<Boolean> = PublishProcessor.create()
    private var pauseConvertToFiatObservable: Boolean = false
    val convertToFiatObserver: Flowable<Boolean> by lazy {
        RxCompoundButton.checkedChanges(conversionSwitch)
                .skipInitialValue()
                .toFlowable(BackpressureStrategy.LATEST)
//                .compose(FlowableTransformers.valve(convertToFiatValve, true))
                .filter {
                    !pauseConvertToFiatObservable
                }
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

    private val fiatName: TextView by lazy {
        rootView.label_fiat
    }

    private val fiatImage: ImageView by lazy {
        rootView.image_fiat
    }

    private val conversionSwitch: SwitchCompat
        get() = rootView.switch_convert_from_fiat

    private lateinit var tickerAdapter: TickerListAdapter

    override fun getRootViewLayoutId(): Int {
        return R.layout.vu_converter
    }

    override fun onCreate() {
        super.onCreate()

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        tickerAdapter = TickerListAdapter(tickerSelectPublisher)
        listView.adapter = tickerAdapter
        listView.layoutManager = LinearLayoutManager(activity)

        bitcoinInput.addTextChangedListener(bitcoinInputWatcher)
        fiatInput.addTextChangedListener(fiatInputWatcher)

        UiUtils.setRipple(rootView.clicker_bitcoin_info)
    }


    fun updateCalculation(calc: CalculationViewModel, initPhase: Boolean = false) {
        val convertFromFiat = !calc.convertToFiat
        if (conversionSwitch.isChecked != convertFromFiat) {
            pauseConvertToFiatObservable = true
            conversionSwitch.isChecked = convertFromFiat
            pauseConvertToFiatObservable = false
        }

        if (initPhase || calc.convertToFiat) {
            fiatInput.removeTextChangedListener(fiatInputWatcher)
            fiatInput.setText(calc.ticker?.price ?: UiUtils.PLACE_HOLDER_STRING)
            fiatInput.addTextChangedListener(fiatInputWatcher)
        }

        if (initPhase || !calc.convertToFiat) {
            bitcoinInput.removeTextChangedListener(bitcoinInputWatcher)
            bitcoinInput.setText(calc.bitcoinPrice)
            bitcoinInput.addTextChangedListener(bitcoinInputWatcher)
        }

        val nuFiatName = calc.ticker?.name ?: UiUtils.PLACE_HOLDER_STRING
        if (fiatName.text != nuFiatName) {
            fiatName.text = nuFiatName

            if (calc.ticker != null) {
                fiatImage.setImageResource(UiUtils.getCurrencyVectorRes(calc.ticker.code))
            } else {
                fiatImage.setImageDrawable(null)
            }
        }
    }


    fun updateTickers(tickers: List<TickerViewModel>) {
        tickerAdapter.items = tickers
        tickerAdapter.notifyDataSetChanged()
    }
}