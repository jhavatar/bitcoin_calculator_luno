package io.chthonic.bitcoin.calculator.utils

import io.chthonic.bitcoin.calculator.data.model.CryptoCurrency
import io.chthonic.bitcoin.calculator.data.model.FiatCurrency
import io.chthonic.bitcoin.calculator.data.model.Ticker
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


/**
 * Created by jhavatar on 3/25/2018.
 */
//@RunWith(RobolectricTestRunner::class)
class ExchangeUtilsTest {

    @Test
    fun testGetFiatCurrencyForTicker() {
        FiatCurrency.values.forEach { fiat ->
            assertEquals(ExchangeUtils.getFiatCurrencyForTicker(Ticker(pair = CryptoCurrency.Bitcoin.code + fiat.code,
                    timestamp = 1,
                    ask = "",
                    bid = "",
                    rolling_24_hour_volume = "",
                    last_trade = "")), fiat)
        }

        assertNull(ExchangeUtils.getFiatCurrencyForTicker(Ticker(pair = CryptoCurrency.Bitcoin.code + "foo",
                timestamp = 1,
                ask = "",
                bid = "",
                rolling_24_hour_volume = "",
                last_trade = "")))
    }


    @Test
    fun testIsSupportedFiatCurrency() {
        FiatCurrency.values.forEach { fiat ->
            assertTrue(ExchangeUtils.isSupportedFiatCurrency(Ticker(pair = CryptoCurrency.Bitcoin.code + fiat.code,
                    timestamp = 1,
                    ask = "",
                    bid = "",
                    rolling_24_hour_volume = "",
                    last_trade = "")))
        }

        assertFalse(ExchangeUtils.isSupportedFiatCurrency(Ticker(pair = CryptoCurrency.Bitcoin.code,
                timestamp = 1,
                ask = "",
                bid = "",
                rolling_24_hour_volume = "",
                last_trade = "")))

        assertFalse(ExchangeUtils.isSupportedFiatCurrency(Ticker(pair = CryptoCurrency.Bitcoin.code + "foo",
                timestamp = 1,
                ask = "",
                bid = "",
                rolling_24_hour_volume = "",
                last_trade = "")))
    }


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