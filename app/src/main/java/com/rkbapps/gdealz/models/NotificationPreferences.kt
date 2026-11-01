package com.rkbapps.gdealz.models

/**
 * User's notification choices.
 *
 * [platforms] and [types] follow the same convention as [FreeDealsFilter]: an empty list means
 * "no restriction", not "nothing selected".
 */
data class NotificationPreferences(
    val giveawayEnabled: Boolean = true,
    val priceDropEnabled: Boolean = true,
    val platforms: List<String> = emptyList(),
    val types: List<String> = emptyList(),
    /** Whole currency units. 0 disables the threshold. */
    val minWorth: Int = 0,
    val quietHoursEnabled: Boolean = false,
    /** Hour of day, 0..23. */
    val quietHoursStart: Int = 22,
    val quietHoursEnd: Int = 8,
)
