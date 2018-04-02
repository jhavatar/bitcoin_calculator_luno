package io.chthonic.bitcoin.calculator.utils

import io.chthonic.bitcoin.calculator.BuildConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigDecimal
import kotlin.test.assertEquals


/**
 * Created by jhavatar on 4/2/2018.
 */

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class TextUtilsTest {

    @Test
    fun testFormatCurrency() {
        var amount:BigDecimal? = null
        val fallback = "foo"

        // test fallback
        assertEquals(TextUtils.formatCurrency(amount, fallback, true), fallback)
        assertEquals(TextUtils.formatCurrency(amount, fallback, false), fallback)

        // test fiat
        amount = 0.0002.toBigDecimal()
        var result: String = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.00")

        amount = 0.002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.00")

        amount = 0.00.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.00")

        amount = 0.02.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.02")

        amount = 0.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "0.00")

        amount = 1.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "1.00")

        amount = 9.99.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "9.99")

        amount = 123.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "123.00")

        amount = 1234.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "1 234.00")

        amount = 123456.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "123 456.00")

        amount = 1234567.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "1 234 567.00")


        // test crypto
        amount = 0.000000002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.00")

        amount = 0.00000002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.00000002")

        amount = 0.0002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.0002")

        amount = 0.002.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.002")

        amount = 0.00.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.00")

        amount = 0.02.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.02")

        amount = 0.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, true)
        assertEquals(result, "0.00")

        amount = 1.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "1.00")

        amount = 9.99.toBigDecimal()
        result = TextUtils.formatCurrency(amount, fallback, false)
        assertEquals(result, "9.99")
    }


    @Test
    fun testDeFormatCurrency() {
        assertEquals(TextUtils.deFormatCurrency(""), "")
        assertEquals(TextUtils.deFormatCurrency(" "), "")
        assertEquals(TextUtils.deFormatCurrency("          "), "")
        assertEquals(TextUtils.deFormatCurrency(TextUtils.PLACE_HOLDER_STRING), "")
        assertEquals(TextUtils.deFormatCurrency("${TextUtils.PLACE_HOLDER_STRING}${TextUtils.PLACE_HOLDER_STRING}${TextUtils.PLACE_HOLDER_STRING}"), "")

        assertEquals(TextUtils.deFormatCurrency("0.00000000"), "0.00000000")
        assertEquals(TextUtils.deFormatCurrency("0.00"), "0.00")
        assertEquals(TextUtils.deFormatCurrency("1"), "1")
        assertEquals(TextUtils.deFormatCurrency("123.00"), "123.00")
        assertEquals(TextUtils.deFormatCurrency("1 234.00"), "1234.00")
        assertEquals(TextUtils.deFormatCurrency("1 234 567.00"), "1234567.00")
    }


    @Test
    fun testGetDateTimeString() {
        var dateTime = "13:12:20"
        var date = TextUtils.timeFormatter.parse(dateTime)
        assertEquals(TextUtils.getDateTimeString(date.time), dateTime)

        dateTime = "2001-02-03"
        date = TextUtils.dateFormatter.parse(dateTime)
        assertEquals(TextUtils.getDateTimeString(date.time), dateTime)
    }
}