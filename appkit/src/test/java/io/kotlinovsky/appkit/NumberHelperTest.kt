package io.kotlinovsky.appkit

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberHelperTest {

    @Test
    fun testNumberFormatting() {
        assertEquals("0", formatNumber(0, false))
        assertEquals("999", formatNumber(999, false))
        assertEquals("1K", formatNumber(1_000, false))
        assertEquals("1.2K", formatNumber(1_200, false))
        assertEquals("1.2K", formatNumber(1_250, false))
        assertEquals("1.2K", formatNumber(1_299, false))
        assertEquals("10K", formatNumber(10_000, false))
        assertEquals("10.2K", formatNumber(10_299, false))
        assertEquals("99.9K", formatNumber(99_999, false))
        assertEquals("999.9K", formatNumber(999_999, false))
        assertEquals("1M", formatNumber(1_000_000, false))
        assertEquals("1.2M", formatNumber(1_200_000, false))
        assertEquals("10.2M", formatNumber(10_200_000, false))
        assertEquals("100.2M", formatNumber(100_200_000, false))
        assertEquals("999.2M", formatNumber(999_200_000, false))
        assertEquals("1B", formatNumber(1_000_000_000, false))
        assertEquals("1.2B", formatNumber(1_200_000_000, false))

        assertEquals("0", formatNumber(0, true))
        assertEquals("999", formatNumber(999, true))
        assertEquals("1K", formatNumber(1_000, true))
        assertEquals("1,2K", formatNumber(1_200, true))
        assertEquals("1,2K", formatNumber(1_250, true))
        assertEquals("1,2K", formatNumber(1_299, true))
        assertEquals("10K", formatNumber(10_000, true))
        assertEquals("10,2K", formatNumber(10_299, true))
        assertEquals("99,9K", formatNumber(99_999, true))
        assertEquals("999,9K", formatNumber(999_999, true))
        assertEquals("1M", formatNumber(1_000_000, true))
        assertEquals("1,2M", formatNumber(1_200_000, true))
        assertEquals("10,2M", formatNumber(10_200_000, true))
        assertEquals("100,2M", formatNumber(100_200_000, true))
        assertEquals("999,2M", formatNumber(999_200_000, true))
        assertEquals("1B", formatNumber(1_000_000_000, true))
        assertEquals("1,2B", formatNumber(1_200_000_000, true))
    }
}