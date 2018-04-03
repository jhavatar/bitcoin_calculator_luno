package io.chthonic.bitcoin.calculator.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import io.chthonic.bitcoin.calculator.R
import io.chthonic.bitcoin.calculator.data.model.CryptoCurrency
import io.chthonic.bitcoin.calculator.data.model.Currency
import io.chthonic.bitcoin.calculator.data.model.FiatCurrency


/**
 * Created by jhavatar on 3/30/2018.
 */
object UiUtils {

    private val mapCurrencyToSign: Map<String, String> by lazy {
        mapOf( Pair(CryptoCurrency.Bitcoin.code,
                        if (UiUtils.canRenderGlyp("\u20BF")) "\u20BF" else "B"),
                Pair(CryptoCurrency.Ethereum.code,
                        if (UiUtils.canRenderGlyp("\u039E")) "\u039E" else "E"),
                Pair(FiatCurrency.Zar.code, "\u0052"),
                Pair(FiatCurrency.Myr.code, "\u0052\u004d"),
                Pair(FiatCurrency.Idr.code, "\u0052\u0070"),
                Pair(FiatCurrency.Ngn.code,
                        if (UiUtils.canRenderGlyp("\u20a6")) "\u20a6" else "N"))
    }

    fun dipToPixels(dip: Int, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun spToPixels(sp: Int, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics).toInt()
    }

    fun canRenderGlyp(s: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Paint().hasGlyph(s)

        } else {
            EmojiRenderableChecker().isRenderable(s)
        }
    }

    fun getCurrencySign(currency: Currency?, fallback: String? = null): String {
        return getCurrencySign(currency?.code, fallback)
    }

    fun getCurrencySign(code: String?, fallback: String? = null): String {
        return mapCurrencyToSign[code] ?: (fallback ?: throw RuntimeException("code $code should not exist"))
    }

    fun getCurrencyVectorRes(code: String): Int {
        return when (code) {
            CryptoCurrency.Bitcoin.code -> R.drawable.ic_xbt
            FiatCurrency.Zar.code -> R.drawable.ic_zar
            FiatCurrency.Myr.code -> R.drawable.ic_myr
            FiatCurrency.Idr.code -> R.drawable.ic_idr
            FiatCurrency.Ngn.code -> R.drawable.ic_ngn
            else -> throw RuntimeException("code $code should not exist")
        }
    }

    fun getFiatImageSmallRes(code: String): Int {
        return when (code) {
            FiatCurrency.Zar.code -> R.drawable.ic_zar_320px
            FiatCurrency.Myr.code -> R.drawable.ic_myr_320px
            FiatCurrency.Idr.code -> R.drawable.ic_idr_320px
            FiatCurrency.Ngn.code -> R.drawable.ic_ngn_320px
            else -> throw RuntimeException("code $code should not exist")
        }
    }

    /**
     * note, view requires android:background="?android:attr/selectableItemBackground"
     */
    fun setRipple(view: View) {
        val attrs = intArrayOf(R.attr.selectableItemBackground)
        val typedArray = view.context.obtainStyledAttributes(attrs)
        val backgroundResource = typedArray.getResourceId(0, 0)
        view.setBackgroundResource(backgroundResource)
    }


    fun getCompoundDrawableForTextDrawable(text: String, tv: TextView, color: Int): TextDrawable {
        val bounds = Rect()
        val textPaint = tv.getPaint()
        textPaint.getTextBounds(text, 0, text.length, bounds)
        val width = bounds.width()

        return TextDrawable.builder()
                .beginConfig()
                .textColor(color)
                .fontSize(tv.textSize.toInt()) // size in px
                .useFont(tv.typeface)
                .width(width) // size in px
                .endConfig()
                .buildRect(text, Color.TRANSPARENT)
    }


}