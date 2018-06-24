package io.chthonic.bitcoin.calculator.ui.vu

import android.app.Activity
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import io.chthonic.bitcoin.calculator.R
import io.chthonic.bitcoin.calculator.data.model.Currency
import io.chthonic.bitcoin.calculator.ui.adapter.TickerListAdapter
import io.chthonic.bitcoin.calculator.ui.model.CalculationViewModel
import io.chthonic.bitcoin.calculator.ui.model.TickerViewModel
import io.chthonic.bitcoin.calculator.ui.view.CurrencyInputWatcher
import io.chthonic.bitcoin.calculator.utils.ExchangeUtils
import io.chthonic.bitcoin.calculator.utils.TextUtils
import io.chthonic.bitcoin.calculator.utils.UiUtils
import io.chthonic.mythos.mvp.FragmentWrapper
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.vu_main.view.*
import timber.log.Timber

/**
 * Created by jhavatar on 3/28/2018.
 */
class MainVu(inflater: LayoutInflater,
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

    private val leftInputPublisher: PublishSubject<String> by lazy {
        PublishSubject.create<String>()
    }
    val leftInputObserver: Flowable<String>
        get() = leftInputPublisher
                .hide()
                .toFlowable(BackpressureStrategy.LATEST)

    private val leftInputWatcher by lazy {
        CurrencyInputWatcher(leftInput, leftInputPublisher, leftInputLength)
    }

    private val rightInputPublisher: PublishSubject<String> by lazy {
        PublishSubject.create<String>()
    }

    val rightInputObserver: Flowable<String>
        get() = rightInputPublisher
                .hide()
                .toFlowable(BackpressureStrategy.LATEST)

    private val rightInputWatcher by lazy {
        CurrencyInputWatcher(rightInput, rightInputPublisher, rightInputLength)
    }

    private val listView: RecyclerView by lazy {
        rootView.list_tickers
    }

    private val leftInput: EditText by lazy {
        rootView.input_left
    }

    private val leftInputLength: Int by lazy {
        val lengthFilter: InputFilter.LengthFilter? = leftInput.filters.find {
            it is InputFilter.LengthFilter
        } as? InputFilter.LengthFilter
        lengthFilter?.max ?: Int.MAX_VALUE
    }

    private val rightInput: EditText by lazy {
        rootView.input_right
    }

    private val rightInputLength: Int by lazy {
        val lengthFilter: InputFilter.LengthFilter? = rightInput.filters.find {
            it is InputFilter.LengthFilter
        } as? InputFilter.LengthFilter
        lengthFilter?.max ?: Int.MAX_VALUE
    }

    private val leftInfoLayout: View by lazy {
        rootView.layout_left_info
    }

    private val leftName: TextView by lazy {
        rootView.label_left
    }

    private val leftImage: ImageView by lazy {
        rootView.image_left
    }

    private val rightInfoLayout: View by lazy {
        rootView.layout_right_info
    }

    private val rightName: TextView by lazy {
        rootView.label_right
    }

    private val rightImage: ImageView by lazy {
        rootView.image_right
    }

    private val tickerAdapter: TickerListAdapter by lazy {
        TickerListAdapter(tickerSelectPublisher)
    }

    override fun getRootViewLayoutId(): Int {
        return R.layout.vu_main
    }

    override fun onCreate() {
        super.onCreate()

        (activity as AppCompatActivity).setSupportActionBar(rootView.toolbar)

        listView.adapter = tickerAdapter
        listView.layoutManager = LinearLayoutManager(activity)
        val interItemPadding = listView.resources.getDimensionPixelSize(R.dimen.content_padding)
        listView.addItemDecoration(object:RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect?.top = interItemPadding
            }
        })

        leftInput.setCompoundDrawablesRelative(UiUtils.getCompoundDrawableForTextDrawable(UiUtils.getCurrencySign(Currency.Bitcoin),
                leftInput,
                leftInput.currentTextColor), null,null, null)

        if (UiUtils.isHorizontal(rootView.resources)) {
            rootView.app_bar.setExpanded(false)
        }

        RxView.clicks(rootView.clicker_left_info)
                .map {
                    TextUtils.deFormatCurrency(leftInput.text.toString())
                }.subscribe(leftInputPublisher)

        RxView.clicks(rootView.clicker_right_info)
                .map {
                    TextUtils.deFormatCurrency(rightInput.text.toString())
                }.subscribe(rightInputPublisher)

        RxView.focusChanges(leftInput)
                .skipInitialValue()
                .filter {
                    it && !leftInfoLayout.isActivated
                }.map {
                    TextUtils.deFormatCurrency(leftInput.text.toString())
                }.subscribe(leftInputPublisher)

        RxView.focusChanges(rightInput)
                .skipInitialValue()
                .filter {
                    it && !rightInfoLayout.isActivated
                }.map {
                    TextUtils.deFormatCurrency(rightInput.text.toString())
                }.subscribe(rightInputPublisher)
    }


    private fun updateActivated(leftTickerIsSource: Boolean): Boolean {
        val activatedChange = (leftInfoLayout.isActivated != leftTickerIsSource) || (rightInfoLayout.isActivated != !leftTickerIsSource)
        if (!activatedChange) {
            return false
        }

        leftInfoLayout.isActivated = leftTickerIsSource
        rootView.layout_left_input.isActivated = leftTickerIsSource
        rightInfoLayout.isActivated = !leftTickerIsSource
        rootView.layout_right_input.isActivated = !leftTickerIsSource

        leftInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                UiUtils.getCompoundDrawableForTextDrawable(
                        UiUtils.getCurrencySign(Currency.Bitcoin),
                        leftInput,
                        if (leftTickerIsSource) leftInput.resources.getColor(R.color.secondaryColor) else leftInput.currentTextColor),
                null,null, null)

        return true
    }


    fun updateCalculation(calc: CalculationViewModel) {
        Timber.d("updateCalculation: calc = $calc")
        if (leftInput.isFocused != calc.leftTickerIsSource) {
            leftInput.requestFocus()
        }
        if (rightInput.isFocused != !calc.leftTickerIsSource) {
            rightInput.requestFocus()
        }

        val convertDirectChanged = updateActivated(calc.leftTickerIsSource)

        if (calc.forceSet || calc.leftTickerIsSource) {
            rightInput.removeTextChangedListener(rightInputWatcher)
            val text = calc.rightTicker?.price ?: TextUtils.PLACE_HOLDER_STRING
            val tooManyDigits = (text.length > rightInputLength)

            if (tooManyDigits) {
                rightInput.setText(TextUtils.TOO_MANY_DIGITS_MSG)

            } else {
                rightInput.setText(text)
            }

            if (text == TextUtils.PLACE_HOLDER_STRING) {
                rightInput.isEnabled = false

            } else {

                // must be able to change text if selected
                if (!tooManyDigits || !calc.leftTickerIsSource) {
                    rightInput.addTextChangedListener(rightInputWatcher)
                    rightInput.isEnabled = true

                } else {
                    rightInput.isEnabled = false
                }
            }
        }

        if (calc.forceSet || !calc.leftTickerIsSource) {
            leftInput.removeTextChangedListener(leftInputWatcher)
            val text = calc.leftTicker?.price ?: TextUtils.PLACE_HOLDER_STRING
//            Timber.d("setBitcoin: text = $text, length = ${text.length}, maxLength = ${leftInputLength}")
            if (text.length > leftInputLength) {
                leftInput.setText(TextUtils.TOO_MANY_DIGITS_MSG)
                if (calc.leftTickerIsSource) {
                    // must be able to change text if selected
                    leftInput.addTextChangedListener(leftInputWatcher)

                } else {
                    leftInput.isEnabled = false
                }

            } else {
                leftInput.isEnabled = true
                leftInput.setText(text)
                leftInput.addTextChangedListener(leftInputWatcher)
            }
        }

        val nuLeftName = calc.leftTicker?.name ?: TextUtils.PLACE_HOLDER_STRING
        val leftNameChanged = leftName.text != nuLeftName
        // update left image and label
        if (leftNameChanged) {
            leftName.text = nuLeftName
            if (calc.leftTicker != null) {
                leftImage.setImageResource(UiUtils.getCurrencyVectorRes(calc.leftTicker.code))

            } else {
                leftImage.setImageDrawable(null)
            }
        }

        // update right compound image
        if (leftNameChanged || convertDirectChanged)  {
            if (calc.leftTicker != null) {
                leftInputWatcher.updateInputType(calc.leftTicker.decimalDigits)
                leftInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        UiUtils.getCompoundDrawableForTextDrawable(
                                UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(calc.leftTicker.code)),
                                leftInput,
                                if (calc.leftTickerIsSource) leftInput.resources.getColor(R.color.secondaryColor) else leftInput.currentTextColor),
                        null, null, null)

            } else {
                leftInput.setCompoundDrawablesRelative(null, null, null, null)
            }
        }


        val nuRightName = calc.rightTicker?.name ?: TextUtils.PLACE_HOLDER_STRING
        val rightNameChanged = rightName.text != nuRightName

        // update right image and label
        if (rightNameChanged) {
            rightName.text = nuRightName
            if (calc.rightTicker != null) {
                rightImage.setImageResource(UiUtils.getCurrencyVectorRes(calc.rightTicker.code))

            } else {
                rightImage.setImageDrawable(null)
            }
        }

        // update right compound image
        if (rightNameChanged || convertDirectChanged)  {
            if (calc.rightTicker != null) {
                rightInputWatcher.updateInputType(calc.rightTicker.decimalDigits)
                rightInput.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        UiUtils.getCompoundDrawableForTextDrawable(
                                UiUtils.getCurrencySign(ExchangeUtils.getCurrencyForTicker(calc.rightTicker.code)),
                                rightInput,
                                if (!calc.leftTickerIsSource) rightInput.resources.getColor(R.color.secondaryColor) else rightInput.currentTextColor),
                        null, null, null)

            } else {
                rightInput.setCompoundDrawablesRelative(null, null, null, null)
            }
        }
    }


    fun updateTickers(tickers: List<TickerViewModel>) {
        tickerAdapter.items = tickers
        tickerAdapter.notifyDataSetChanged()
    }
}