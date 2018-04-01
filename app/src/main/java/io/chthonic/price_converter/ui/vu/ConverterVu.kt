package io.chthonic.price_converter.ui.vu

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import io.chthonic.mythos.mvp.FragmentWrapper
import io.chthonic.price_converter.R
import io.chthonic.price_converter.data.model.CalculationViewModel
import io.chthonic.price_converter.data.model.CryptoCurrency
import io.chthonic.price_converter.data.model.TickerViewModel
import io.chthonic.price_converter.ui.adapter.TickerListAdapter
import io.chthonic.price_converter.utils.ExchangeUtils
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
                if (s?.toString() == UiUtils.PLACE_HOLDER_STRING) {
                    return
                }

                bitcoinInput.removeTextChangedListener(this)

                val sRaw = UiUtils.deFormatCurrency(s?.toString() ?: "")
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
                if (s?.toString() == UiUtils.PLACE_HOLDER_STRING) {
                    return
                }

                fiatInput.removeTextChangedListener(this)

                val sRaw = UiUtils.deFormatCurrency(s?.toString() ?: "")
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

    private val bitcoinInfoLayout: View by lazy {
        rootView.layout_bitcoin_info
    }

    private val fiatInfoLayout: View by lazy {
        rootView.layout_fiat_info
    }

    private val fiatName: TextView by lazy {
        rootView.label_fiat
    }

    private val fiatImage: ImageView by lazy {
        rootView.image_fiat
    }


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

        bitcoinInput.setCompoundDrawablesRelative(UiUtils.getCompoundDrawableForTextDrawable(CryptoCurrency.Bitcoin.sign, bitcoinInput), null,null, null)

        UiUtils.setRipple(rootView.clicker_bitcoin_info)
        RxView.clicks(rootView.clicker_bitcoin_info)
                .map {
                    UiUtils.deFormatCurrency(bitcoinInput.text.toString())
                }.subscribe(bitcoinInputPublisher)

        UiUtils.setRipple(rootView.clicker_fiat_info)
        RxView.clicks(rootView.clicker_fiat_info)
                .map {
                    UiUtils.deFormatCurrency(fiatInput.text.toString())
                }.subscribe(fiatInputPublisher)

        RxView.focusChanges(bitcoinInput)
                .skipInitialValue()
                .filter {
                    it && !bitcoinInfoLayout.isActivated
                }.map {
                    UiUtils.deFormatCurrency(bitcoinInput.text.toString())
                }.subscribe(bitcoinInputPublisher)

        RxView.focusChanges(fiatInput)
                .skipInitialValue()
                .filter {
                    it && !fiatInfoLayout.isActivated
                }.map {
                    UiUtils.deFormatCurrency(fiatInput.text.toString())
                }.subscribe(fiatInputPublisher)
    }


    private fun updateActivated(convertToFiat: Boolean) {
        if (bitcoinInfoLayout.isActivated != convertToFiat) {
            bitcoinInfoLayout.isActivated = convertToFiat
        }
        if (fiatInfoLayout.isActivated != !convertToFiat) {
            fiatInfoLayout.isActivated = !convertToFiat
        }
        if (rootView.layout_bitcoin_input.isActivated != convertToFiat) {
            rootView.layout_bitcoin_input.isActivated = convertToFiat
        }
        if (rootView.layout_fiat_input.isActivated != !convertToFiat) {
            rootView.layout_fiat_input.isActivated = !convertToFiat
        }
        if (bitcoinInput.isFocused != convertToFiat) {
            bitcoinInput.requestFocus()
        }
        if (fiatInput.isFocused != !convertToFiat) {
            fiatInput.requestFocus()
        }
    }


    fun updateCalculation(calc: CalculationViewModel, initPhase: Boolean = false) {
        updateActivated(calc.convertToFiat)

        if (rootView.layout_bitcoin_info.isActivated != calc.convertToFiat) {
            rootView.layout_bitcoin_info.isActivated = calc.convertToFiat
        }
        if (rootView.layout_fiat_info.isActivated != !calc.convertToFiat) {
            rootView.layout_fiat_info.isActivated = !calc.convertToFiat
        }
        if (rootView.input_btx.isFocused != calc.convertToFiat) {
            rootView.input_btx.requestFocus()
        }
        if (rootView.input_fiat.isFocused != !calc.convertToFiat) {
            rootView.input_fiat.requestFocus()
        }

        if (initPhase || calc.convertToFiat) {
            fiatInput.removeTextChangedListener(fiatInputWatcher)
            val text = calc.ticker?.price ?: UiUtils.PLACE_HOLDER_STRING
            fiatInput.setText(text)
            if (text != UiUtils.PLACE_HOLDER_STRING) {
                fiatInput.addTextChangedListener(fiatInputWatcher)
                fiatInput.isEnabled = true
                
            } else {
                fiatInput.isEnabled = false
            }
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
                fiatInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        UiUtils.getCompoundDrawableForTextDrawable(
                                ExchangeUtils.getFiatCurrencyForTicker(calc.ticker.code)!!.sign, fiatInput),
                        null,null, null)

            } else {
                fiatImage.setImageDrawable(null)
                fiatInput.setCompoundDrawablesRelative(null, null, null, null)
            }
        }
    }


    fun updateTickers(tickers: List<TickerViewModel>) {
        tickerAdapter.items = tickers
        tickerAdapter.notifyDataSetChanged()
    }
}