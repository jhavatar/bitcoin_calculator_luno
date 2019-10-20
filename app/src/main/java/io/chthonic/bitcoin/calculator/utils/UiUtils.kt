package io.chthonic.bitcoin.calculator.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.ResourcesCompat
import com.amulyakhare.textdrawable.TextDrawable
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import io.chthonic.bitcoin.calculator.R
import io.chthonic.bitcoin.calculator.data.model.CryptoCurrency
import io.chthonic.bitcoin.calculator.data.model.Currency
import io.chthonic.bitcoin.calculator.data.model.FiatCurrency


/**
 * Created by jhavatar on 3/30/2018.
 */
object UiUtils {

    private val mapCurrencyToSign: Map<String, String> by lazy {
        mapOf( Pair(CryptoCurrency.Bitcoin.code, "{cmd_currency_btc}"),
                Pair(CryptoCurrency.Ethereum.code, "{cmd-currency-eth}"),
                Pair(FiatCurrency.Zar.code, "\u0052"),
                Pair(FiatCurrency.Myr.code, "\u0052\u004d"),
                Pair(FiatCurrency.Idr.code, "\u0052\u0070"),
                Pair(FiatCurrency.Ngn.code, "{cmd_currency_ngn}"),
                Pair(FiatCurrency.Zmw.code, "\u004B"),
                Pair(FiatCurrency.Eur.code, "{cmd-currency-eur}"),
                Pair(FiatCurrency.Ugx.code, "\u0055\u0053\u0068"))
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
            FiatCurrency.Zmw.code -> R.drawable.ic_zmw
            FiatCurrency.Eur.code -> R.drawable.ic_eur
            FiatCurrency.Ugx.code -> R.drawable.ic_ugx
            else -> throw RuntimeException("code $code should not exist")
        }
    }

    fun getFiatImageSmallRes(code: String): Int {
        return when (code) {
            FiatCurrency.Zar.code -> R.drawable.ic_zar_320px
            FiatCurrency.Myr.code -> R.drawable.ic_myr_320px
            FiatCurrency.Idr.code -> R.drawable.ic_idr_320px
            FiatCurrency.Ngn.code -> R.drawable.ic_ngn_320px
            FiatCurrency.Zmw.code -> R.drawable.ic_zmw_320px
            FiatCurrency.Eur.code -> R.drawable.ic_eur_320px
            FiatCurrency.Ugx.code -> R.drawable.ic_ugx_320px
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
        typedArray.recycle()
    }


    fun getCompoundDrawableForTextDrawable(text: String, tv: TextView, color: Int): Drawable {
        return if (text.startsWith("{")) {
            IconicsDrawable(tv.context)
                    // use Icon reference since Iconics' icon(String) is buggy
                    .icon(when (text) {
                        mapCurrencyToSign[CryptoCurrency.Ethereum.code] -> CommunityMaterial.Icon.cmd_currency_eth
                        mapCurrencyToSign[FiatCurrency.Eur.code] -> CommunityMaterial.Icon.cmd_currency_eur
                        mapCurrencyToSign[FiatCurrency.Ngn.code] -> CommunityMaterial.Icon.cmd_currency_ngn
                        else -> CommunityMaterial.Icon.cmd_currency_btc
                    })
                    .color(IconicsColor.colorInt(color))
                    .size(IconicsSize.px(tv.textSize.toInt()))


        } else {
            val bounds = Rect()
            val textPaint = tv.getPaint()
            textPaint.getTextBounds(text, 0, text.length, bounds)
            val width = bounds.width()

            TextDrawable.builder()
                    .beginConfig()
                    .textColor(color)
                    .fontSize(tv.textSize.toInt()) // size in px
                    .useFont(tv.typeface)
                    .width(width) // size in px
                    .endConfig()
                    .buildRect(text, Color.TRANSPARENT)
        }
    }


    fun isHorizontal(res: Resources): Boolean {
        return res.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }


    fun showUrl(context: Context, url: String) {
        val resources = context.resources
        val builder = CustomTabsIntent.Builder()
                .setToolbarColor(ResourcesCompat.getColor(resources, R.color.primaryColor, context.theme))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

}