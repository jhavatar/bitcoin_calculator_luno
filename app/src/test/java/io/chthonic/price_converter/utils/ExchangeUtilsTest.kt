package io.chthonic.price_converter.utils

import io.chthonic.price_converter.data.model.Ticker
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import kotlin.test.assertEquals


/**
 * Created by jhavatar on 3/25/2018.
 */
//@RunWith(RobolectricTestRunner::class)
class ExchangeUtilsTest {

    @Test
    fun testConvertToFiat() {
        val ticker = Ticker(10, "10", "11", "12", "foo", "bar")
        val fiatVal = ExchangeUtils.convertToFiat(BigDecimal(100), ticker)
        assertEquals(fiatVal, BigDecimal(100).multiply(ticker.bid.toBigDecimal()))
    }


    @Test
    fun testConvertToBitcoin() {
        val ticker = Ticker(10, "10", "11", "12", "foo", "bar")
        val fiatVal = ExchangeUtils.convertToBitcoin(BigDecimal(100), ticker)
        assertEquals(fiatVal, BigDecimal(100).divide(ticker.ask.toBigDecimal(), MathContext.DECIMAL128))
    }

}