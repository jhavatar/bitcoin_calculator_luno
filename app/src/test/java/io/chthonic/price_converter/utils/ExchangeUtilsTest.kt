package io.chthonic.price_converter.utils

import io.chthonic.price_converter.data.model.CalculatorState
import io.chthonic.price_converter.data.model.ExchangeState
import io.chthonic.price_converter.data.model.Ticker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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


    @Test
    fun testConvertToBitcoin() {
        val ticker = Ticker(10, "10", "11", "12", "foo", "bar")
        val fiatVal = ExchangeUtils.convertToBitcoin(BigDecimal(100), ticker)
        assertEquals(fiatVal, BigDecimal(100 * 11))
    }

    @Test
    fun testGetBitcoinPrice() {
        val fooTicker = Ticker(10, "11", "12", "", "", "foo")
        val barTicker = Ticker(14, "15", "16", "", "", "bar")
        val tickers = mapOf(Pair(fooTicker.pair, fooTicker),
                Pair(barTicker.pair, barTicker))
        val exchangeState = ExchangeState(tickers)

        // no conversion required
        var calcState = CalculatorState("bar", true, BigDecimal(100))
        assertEquals(ExchangeUtils.getBitcoinPrice(calcState, exchangeState), calcState.source)

        // convert from fiat foo
        calcState = CalculatorState(fooTicker.pair, false, BigDecimal(100))
        assertEquals(ExchangeUtils.getBitcoinPrice(calcState, exchangeState), calcState.source.multiply(fooTicker.ask.toBigDecimal()))

        // convert from fiat bar
        calcState = CalculatorState(barTicker.pair, false, BigDecimal(100))
        assertEquals(ExchangeUtils.getBitcoinPrice(calcState, exchangeState), calcState.source.multiply(barTicker.ask.toBigDecimal()))

        // convert from non existing fiat
        calcState = CalculatorState("fus", false, BigDecimal(100))
        assertNull(ExchangeUtils.getBitcoinPrice(calcState, exchangeState))

        // convert from from fiat with none selected
        calcState = CalculatorState(null, false, BigDecimal(100))
        assertNull(ExchangeUtils.getBitcoinPrice(calcState, exchangeState))
    }


    @Test
    fun testGetFiatPrice() {
        val fooTicker = Ticker(10, "11", "12", "", "", "foo")
        val barTicker = Ticker(14, "15", "16", "", "", "bar")
        val source = BigDecimal(100)
        val fooBitcoin =  ExchangeUtils.convertToBitcoin(source, fooTicker)
        val barBitcoin =  ExchangeUtils.convertToBitcoin(source, barTicker)
        val tickers = mapOf(Pair(fooTicker.pair, fooTicker),
                Pair(barTicker.pair, barTicker))
        val exchangeState = ExchangeState(tickers)

        // no conversion required
        var calcState = CalculatorState(fooTicker.pair, false, source)
        assertEquals(ExchangeUtils.getFiatPrice(fooTicker, calcState, exchangeState), calcState.source)

        // convert directly from bitcoin (selected)
        calcState = CalculatorState(fooTicker.pair, true, source)
        assertEquals(ExchangeUtils.getFiatPrice(fooTicker, calcState, exchangeState), ExchangeUtils.convertToFiat(calcState.source, fooTicker))

        // convert directly from bitcoin (not selected)
        calcState = CalculatorState(barTicker.pair, true, source)
        assertEquals(ExchangeUtils.getFiatPrice(fooTicker, calcState, exchangeState), ExchangeUtils.convertToFiat(calcState.source, fooTicker))

        // convert to bitcoin then to foo fiat
        calcState = CalculatorState(barTicker.pair, false, source)
        assertEquals(ExchangeUtils.getFiatPrice(fooTicker, calcState, exchangeState), barBitcoin.multiply(fooTicker.bid.toBigDecimal()))

        // convert to bitcoin then to bar fiat
        calcState = CalculatorState(fooTicker.pair, false, source)
        assertEquals(ExchangeUtils.getFiatPrice(barTicker, calcState, exchangeState), fooBitcoin.multiply(barTicker.bid.toBigDecimal()))

        // unknown fiat selected to convert from
        calcState = CalculatorState("fus", false, BigDecimal(100))
        assertNull(ExchangeUtils.getFiatPrice(fooTicker, calcState, exchangeState))

        // no fiat selected to convert from
        calcState = CalculatorState(null, false, BigDecimal(100))
        assertNull(ExchangeUtils.getFiatPrice(fooTicker, calcState, exchangeState))
    }
}