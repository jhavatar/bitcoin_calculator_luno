package io.chthonic.bitcoin.calculator.ui.view

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.chthonic.bitcoin.calculator.utils.TextUtils
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.math.BigDecimal

/**
 * Created by jhavatar on 4/4/2018.
 */
class CurrencyInputWatcher(val inputView: EditText, val inputChangePublisher: PublishSubject<String>, val isCrypto: Boolean, val maxLength: Int) : TextWatcher {
    var prevString = ""
    private var delAction = false
    private var caretPos = 0

    override fun afterTextChanged(editable: Editable?) {
        val s = editable?.toString()
        if (s == null) {
            return
        } else if (TextUtils.isCurrencyInWarningState(s)) {
            prevString = s
            return
        }
        inputView.removeTextChangedListener(this)

        var ignoreChange = false
        val sRaw = if (TextUtils.wasCurrencyWarningState(prevString, delAction)) {
            "0.00"

        } else {
            TextUtils.deFormatCurrency(s ?: "0")
        }
        val sFormatted = TextUtils.formatCurrency(BigDecimal(sRaw), isCrypto = isCrypto).let {
            // do not allow input change if its formatting pushes the length over the limit
            if (it.length <= maxLength) {
                it

            } else {
                ignoreChange = true
                prevString
            }
        }
//        Timber.d("afterTextChanged: sFormatted = $sFormatted, length = ${sFormatted.length}, maxLength = ${maxLength}")
        inputView.setText(sFormatted)
//        Timber.d("afterTextChanged: delAction = $delAction, caretPos = $caretPos")

        if (ignoreChange) {
            inputView.setSelection(Math.max(Math.min(sFormatted.length, caretPos), 0))

        } else if (delAction) {
            inputView.setSelection(Math.max(Math.min(sFormatted.length, caretPos - 1), 0))

        } else {
            inputView.setSelection(Math.min(sFormatted.length, caretPos + 1))
        }
        prevString = sFormatted

        inputView.addTextChangedListener(this)

        if (!ignoreChange) {
            inputChangePublisher.onNext(sRaw)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        caretPos = inputView.selectionStart
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        delAction = s?.length ?: 0 < prevString.length
    }
}