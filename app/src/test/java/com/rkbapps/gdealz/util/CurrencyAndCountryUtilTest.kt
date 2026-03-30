package com.rkbapps.gdealz.util

import com.rkbapps.gdealz.models.deal.Price
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyAndCountryUtilTest {

    // formatPrice — String input (CheapShark API prices)

    @Test
    fun formatPrice_addsThousandSeparator() {
        assertEquals("1,299.99", CurrencyAndCountryUtil.formatPrice("1299.99"))
    }

    @Test
    fun formatPrice_largePrice() {
        assertEquals("10,499.00", CurrencyAndCountryUtil.formatPrice("10499"))
    }

    @Test
    fun formatPrice_veryLargePrice() {
        assertEquals("1,000,000.00", CurrencyAndCountryUtil.formatPrice("1000000"))
    }

    @Test
    fun formatPrice_smallPrice_noSeparator() {
        assertEquals("29.99", CurrencyAndCountryUtil.formatPrice("29.99"))
    }

    @Test
    fun formatPrice_singleDigit() {
        assertEquals("5.00", CurrencyAndCountryUtil.formatPrice("5"))
    }

    @Test
    fun formatPrice_withDecimals() {
        assertEquals("0.99", CurrencyAndCountryUtil.formatPrice("0.99"))
    }

    @Test
    fun formatPrice_wholeNumber() {
        assertEquals("1,000.00", CurrencyAndCountryUtil.formatPrice("1000"))
    }

    @Test
    fun formatPrice_zero() {
        assertEquals("0.00", CurrencyAndCountryUtil.formatPrice("0"))
    }

    @Test
    fun formatPrice_zeroPointZero() {
        assertEquals("0.00", CurrencyAndCountryUtil.formatPrice("0.00"))
    }

    @Test
    fun formatPrice_null_returnsNA() {
        assertEquals("N/A", CurrencyAndCountryUtil.formatPrice(null))
    }

    @Test
    fun formatPrice_emptyString_returnsOriginal() {
        assertEquals("", CurrencyAndCountryUtil.formatPrice(""))
    }

    @Test
    fun formatPrice_invalidString_returnsOriginal() {
        assertEquals("abc", CurrencyAndCountryUtil.formatPrice("abc"))
    }

    // formatAmount — Double input (IsThereAnyDeal API prices)

    @Test
    fun formatAmount_addsThousandSeparator() {
        assertEquals("1,299.99", CurrencyAndCountryUtil.formatAmount(1299.99))
    }

    @Test
    fun formatAmount_largeValue() {
        assertEquals("12,345.67", CurrencyAndCountryUtil.formatAmount(12345.67))
    }

    @Test
    fun formatAmount_smallValue() {
        assertEquals("9.99", CurrencyAndCountryUtil.formatAmount(9.99))
    }

    @Test
    fun formatAmount_zero() {
        assertEquals("0.00", CurrencyAndCountryUtil.formatAmount(0.0))
    }

    @Test
    fun formatAmount_wholeNumber() {
        assertEquals("50.00", CurrencyAndCountryUtil.formatAmount(50.0))
    }

    @Test
    fun formatAmount_null_returnsNA() {
        assertEquals("N/A", CurrencyAndCountryUtil.formatAmount(null))
    }

    // getCurrencyAndAmount — Price object (used by GameInfoScreen)

    @Test
    fun getCurrencyAndAmount_usd_formatsWithSeparator() {
        val price = Price(amount = 1299.99, currency = "USD")
        assertEquals("\$1,299.99", CurrencyAndCountryUtil.getCurrencyAndAmount(price))
    }

    @Test
    fun getCurrencyAndAmount_eur() {
        val price = Price(amount = 49.99, currency = "EUR")
        assertEquals("€49.99", CurrencyAndCountryUtil.getCurrencyAndAmount(price))
    }

    @Test
    fun getCurrencyAndAmount_gbp() {
        val price = Price(amount = 2500.00, currency = "GBP")
        assertEquals("£2,500.00", CurrencyAndCountryUtil.getCurrencyAndAmount(price))
    }

    @Test
    fun getCurrencyAndAmount_unknownCurrency_usesDash() {
        val price = Price(amount = 10.0, currency = "UNKNOWN")
        assertEquals("-10.00", CurrencyAndCountryUtil.getCurrencyAndAmount(price))
    }

    @Test
    fun getCurrencyAndAmount_nullCurrency_usesDash() {
        val price = Price(amount = 10.0, currency = null)
        assertEquals("-10.00", CurrencyAndCountryUtil.getCurrencyAndAmount(price))
    }

    @Test
    fun getCurrencyAndAmount_nullAmount() {
        val price = Price(amount = null, currency = "USD")
        assertEquals("\$N/A", CurrencyAndCountryUtil.getCurrencyAndAmount(price))
    }

    @Test
    fun getCurrencyAndAmount_nullPrice() {
        assertEquals("-N/A", CurrencyAndCountryUtil.getCurrencyAndAmount(null))
    }
}
