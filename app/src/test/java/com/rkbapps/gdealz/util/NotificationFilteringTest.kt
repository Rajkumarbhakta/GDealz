package com.rkbapps.gdealz.util

import com.rkbapps.gdealz.models.Giveaway
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationFilteringTest {

    private fun giveaway(
        id: Int = 1,
        platforms: String = "PC, Steam",
        type: String = "Game",
        worth: String = "\$29.99",
    ) = Giveaway(
        id = id,
        description = "",
        endDate = "",
        gamerPowerUrl = "",
        image = "",
        instructions = "",
        openGiveaway = "",
        openGiveawayUrl = "",
        platforms = platforms,
        publishedDate = "",
        status = "Active",
        thumbnail = "",
        title = "Test",
        type = type,
        users = 0,
        worth = worth,
    )

    // region quiet hours

    @Test
    fun `non wrapping window is start inclusive and end exclusive`() {
        assertFalse(isWithinQuietHours(9, 17, nowHour = 8))
        assertTrue(isWithinQuietHours(9, 17, nowHour = 9))
        assertTrue(isWithinQuietHours(9, 17, nowHour = 16))
        assertFalse(isWithinQuietHours(9, 17, nowHour = 17))
    }

    @Test
    fun `window wrapping past midnight covers both sides`() {
        assertTrue(isWithinQuietHours(22, 8, nowHour = 23))
        assertTrue(isWithinQuietHours(22, 8, nowHour = 3))
        assertTrue(isWithinQuietHours(22, 8, nowHour = 22))
        assertFalse(isWithinQuietHours(22, 8, nowHour = 9))
        assertFalse(isWithinQuietHours(22, 8, nowHour = 8))
    }

    @Test
    fun `empty window is never quiet`() {
        assertFalse(isWithinQuietHours(8, 8, nowHour = 8))
        assertFalse(isWithinQuietHours(8, 8, nowHour = 20))
    }

    // endregion

    // region worth parsing

    @Test
    fun `parses a plain price`() {
        assertEquals(29.99, "\$29.99".toWorthAmount()!!, 0.001)
    }

    @Test
    fun `parses a price with a thousands separator`() {
        assertEquals(1299.00, "\$1,299.00".toWorthAmount()!!, 0.001)
    }

    @Test
    fun `unpriced giveaways have no amount`() {
        assertNull("N/A".toWorthAmount())
        assertNull("".toWorthAmount())
    }

    // endregion

    // region filter matching

    @Test
    fun `empty selections match everything`() {
        assertTrue(giveaway().matchesFilter(emptyList(), emptyList()))
        assertTrue(giveaway(type = "DLC").matchesFilter(emptyList(), emptyList()))
    }

    @Test
    fun `platform matching is exact, not a substring`() {
        assertTrue(giveaway(platforms = "PC, Steam").matchesFilter(listOf("PC"), emptyList()))
        assertFalse(giveaway(platforms = "PC Game Pass").matchesFilter(listOf("PC"), emptyList()))
    }

    @Test
    fun `type selection excludes other types`() {
        assertTrue(giveaway(type = "Game").matchesFilter(emptyList(), listOf("Game")))
        assertFalse(giveaway(type = "DLC").matchesFilter(emptyList(), listOf("Game")))
    }

    @Test
    fun `both axes must match`() {
        val g = giveaway(platforms = "Epic Games Store", type = "DLC")
        assertFalse(g.matchesFilter(listOf("Steam"), listOf("DLC")))
        assertTrue(g.matchesFilter(listOf("Epic Games Store"), listOf("DLC")))
    }

    @Test
    fun `unpriced giveaways pass any threshold`() {
        assertTrue(giveaway(worth = "N/A").meetsMinWorth(20))
        assertTrue(giveaway(worth = "").meetsMinWorth(20))
    }

    @Test
    fun `min worth compares against the listed value`() {
        assertTrue(giveaway(worth = "\$29.99").meetsMinWorth(20))
        assertFalse(giveaway(worth = "\$9.99").meetsMinWorth(20))
        assertTrue(giveaway(worth = "\$20.00").meetsMinWorth(20))
    }

    @Test
    fun `a zero threshold is disabled`() {
        assertTrue(giveaway(worth = "\$0.99").meetsMinWorth(0))
    }

    // endregion
}
