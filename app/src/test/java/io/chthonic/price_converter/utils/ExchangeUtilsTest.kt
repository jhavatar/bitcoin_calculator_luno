package io.chthonic.price_converter.utils

import io.chthonic.price_converter.data.model.Ticker
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal


/**
 * Created by jhavatar on 3/25/2018.
 */
//@RunWith(RobolectricTestRunner::class)
class ExchangeUtilsTest {

    @Test
    fun testConvertToFiat() {
        val ticker = Ticker(10, "10", "11", "12", "foo", "bar")
        val fiatVal = ExchangeUtils.convertToFiat(BigDecimal(100), ticker)
        assertEquals(fiatVal, BigDecimal(100 * 10))
    }
}