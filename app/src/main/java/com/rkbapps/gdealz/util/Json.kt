package com.rkbapps.gdealz.util

import kotlinx.serialization.json.Json

val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    prettyPrint = true
}