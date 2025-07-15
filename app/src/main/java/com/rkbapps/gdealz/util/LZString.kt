package com.rkbapps.gdealz.util

import kotlin.math.pow

object LZString {
    private const val KEY_STRING_BASE_64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
    private const val KEY_STRING_URI_SAFE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-\$"
    private val baseReverseDic = mutableMapOf<String, MutableMap<Char, Int>>()

    private fun getBaseValue(alphabet: String, character: Char): Int {
        if (!baseReverseDic.containsKey(alphabet)) {
            baseReverseDic[alphabet] = mutableMapOf()
            for (i in alphabet.indices) {
                baseReverseDic[alphabet]!![alphabet[i]] = i
            }
        }
        return baseReverseDic[alphabet]!![character]!!
    }

    fun compressToBase64(input: String?): String? {
        val res = compress(input, 6) { a -> KEY_STRING_BASE_64[a].toString() }
        if (res == null) return null
        return when (res.length % 4) {
            0 -> res
            1 -> "$res==="
            2 -> "$res=="
            3 -> "$res="
            else -> null
        }
    }

    fun decompressFromBase64(input: String?): String? {
        if (input.isNullOrEmpty()) return null
        return decompress(input.length, 32) { index -> getBaseValue(KEY_STRING_BASE_64, input[index]) }
    }

    fun compressToUTF16(input: String?): String? {
        val compressed = compress(input, 15) { a -> (a + 32).toChar().toString() }
        return compressed?.plus(" ")
    }

    fun decompressFromUTF16(compressed: String?): String? {
        if (compressed.isNullOrEmpty()) return null
        return decompress(compressed.length, 16384) { index -> compressed[index].code - 32 }
    }

    fun compressToUint8Array(uncompressed: String?): ByteArray? {
        val compressed = compress(uncompressed)
        if (compressed == null) return null
        val buf = ByteArray(compressed.length * 2)
        for (i in compressed.indices) {
            val currentValue = compressed[i].code
            buf[i * 2] = (currentValue shr 8).toByte()
            buf[i * 2 + 1] = (currentValue % 256).toByte()
        }
        return buf
    }

    fun decompressFromUint8Array(compressed: ByteArray?): String? {
        if (compressed == null) return null
        val buf = IntArray(compressed.size / 2) { i ->
            (compressed[i * 2].toInt() and 0xFF) shl 8 or (compressed[i * 2 + 1].toInt() and 0xFF)
        }
        val result = StringBuilder()
        buf.forEach { c -> result.append(c.toChar()) }
        return decompress(result.toString())
    }

    fun decompressFromEncodedURIComponent(input: String?): String? {
        if (input.isNullOrEmpty()) return null
        val processedInput = input.replace(" ", "+")
        return decompress(processedInput.length, 32) { index -> getBaseValue(KEY_STRING_URI_SAFE, processedInput[index]) }
    }

    fun compressToEncodedURIComponent(input: String?): String? {
        return compress(input, 6) { a -> KEY_STRING_URI_SAFE[a].toString() }
    }

    fun compress(uncompressed: String?): String? {
        return compress(uncompressed, 16) { a -> a.toChar().toString() }
    }

    private fun compress(
        uncompressed: String?,
        bitsPerChar: Int,
        getCharFromInt: (Int) -> String
    ): String? {
        if (uncompressed == null) return null
        var value: Int
        val contextDictionary = mutableMapOf<String, Int>()
        val contextDictionaryToCreate = mutableMapOf<String, Boolean>()
        var contextC = ""
        var contextWC = ""
        var contextW = ""
        var contextEnlargeIn = 2
        var contextDictSize = 3
        var contextNumBits = 2
        val contextData = StringBuilder()
        var contextDataVal = 0
        var contextDataPosition = 0

        for (ii in uncompressed.indices) {
            contextC = uncompressed[ii].toString()
            if (!contextDictionary.containsKey(contextC)) {
                contextDictionary[contextC] = contextDictSize++
                contextDictionaryToCreate[contextC] = true
            }

            contextWC = contextW + contextC
            if (contextDictionary.containsKey(contextWC)) {
                contextW = contextWC
            } else {
                if (contextDictionaryToCreate.containsKey(contextW)) {
                    if (contextW[0].code < 256) {
                        for (i in 0 until contextNumBits) {
                            contextDataVal = contextDataVal shl 1
                            if (contextDataPosition == bitsPerChar - 1) {
                                contextDataPosition = 0
                                contextData.append(getCharFromInt(contextDataVal))
                                contextDataVal = 0
                            } else {
                                contextDataPosition++
                            }
                        }
                        value = contextW[0].code
                        for (i in 0 until 8) {
                            contextDataVal = (contextDataVal shl 1) or (value and 1)
                            if (contextDataPosition == bitsPerChar - 1) {
                                contextDataPosition = 0
                                contextData.append(getCharFromInt(contextDataVal))
                                contextDataVal = 0
                            } else {
                                contextDataPosition++
                            }
                            value = value shr 1
                        }
                    } else {
                        value = 1
                        for (i in 0 until contextNumBits) {
                            contextDataVal = (contextDataVal shl 1) or value
                            if (contextDataPosition == bitsPerChar - 1) {
                                contextDataPosition = 0
                                contextData.append(getCharFromInt(contextDataVal))
                                contextDataVal = 0
                            } else {
                                contextDataPosition++
                            }
                            value = 0
                        }
                        value = contextW[0].code
                        for (i in 0 until 16) {
                            contextDataVal = (contextDataVal shl 1) or (value and 1)
                            if (contextDataPosition == bitsPerChar - 1) {
                                contextDataPosition = 0
                                contextData.append(getCharFromInt(contextDataVal))
                                contextDataVal = 0
                            } else {
                                contextDataPosition++
                            }
                            value = value shr 1
                        }
                    }
                    contextEnlargeIn--
                    if (contextEnlargeIn == 0) {
                        contextEnlargeIn = 2.0.pow(contextNumBits).toInt()
                        contextNumBits++
                    }
                    contextDictionaryToCreate.remove(contextW)
                } else {
                    value = contextDictionary[contextW]!!
                    for (i in 0 until contextNumBits) {
                        contextDataVal = (contextDataVal shl 1) or (value and 1)
                        if (contextDataPosition == bitsPerChar - 1) {
                            contextDataPosition = 0
                            contextData.append(getCharFromInt(contextDataVal))
                            contextDataVal = 0
                        } else {
                            contextDataPosition++
                        }
                        value = value shr 1
                    }
                }
                contextEnlargeIn--
                if (contextEnlargeIn == 0) {
                    contextEnlargeIn = 2.0.pow(contextNumBits).toInt()
                    contextNumBits++
                }
                contextDictionary[contextWC] = contextDictSize++
                contextW = contextC
            }
        }

        if (contextW.isNotEmpty()) {
            if (contextDictionaryToCreate.containsKey(contextW)) {
                if (contextW[0].code < 256) {
                    for (i in 0 until contextNumBits) {
                        contextDataVal = contextDataVal shl 1
                        if (contextDataPosition == bitsPerChar - 1) {
                            contextDataPosition = 0
                            contextData.append(getCharFromInt(contextDataVal))
                            contextDataVal = 0
                        } else {
                            contextDataPosition++
                        }
                    }
                    value = contextW[0].code
                    for (i in 0 until 8) {
                        contextDataVal = (contextDataVal shl 1) or (value and 1)
                        if (contextDataPosition == bitsPerChar - 1) {
                            contextDataPosition = 0
                            contextData.append(getCharFromInt(contextDataVal))
                            contextDataVal = 0
                        } else {
                            contextDataPosition++
                        }
                        value = value shr 1
                    }
                } else {
                    value = 1
                    for (i in 0 until contextNumBits) {
                        contextDataVal = (contextDataVal shl 1) or value
                        if (contextDataPosition == bitsPerChar - 1) {
                            contextDataPosition = 0
                            contextData.append(getCharFromInt(contextDataVal))
                            contextDataVal = 0
                        } else {
                            contextDataPosition++
                        }
                        value = 0
                    }
                    value = contextW[0].code
                    for (i in 0 until 16) {
                        contextDataVal = (contextDataVal shl 1) or (value and 1)
                        if (contextDataPosition == bitsPerChar - 1) {
                            contextDataPosition = 0
                            contextData.append(getCharFromInt(contextDataVal))
                            contextDataVal = 0
                        } else {
                            contextDataPosition++
                        }
                        value = value shr 1
                    }
                }
                contextEnlargeIn--
                if (contextEnlargeIn == 0) {
                    contextEnlargeIn = 2.0.pow(contextNumBits).toInt()
                    contextNumBits++
                }
                contextDictionaryToCreate.remove(contextW)
            } else {
                value = contextDictionary[contextW]!!
                for (i in 0 until contextNumBits) {
                    contextDataVal = (contextDataVal shl 1) or (value and 1)
                    if (contextDataPosition == bitsPerChar - 1) {
                        contextDataPosition = 0
                        contextData.append(getCharFromInt(contextDataVal))
                        contextDataVal = 0
                    } else {
                        contextDataPosition++
                    }
                    value = value shr 1
                }
            }
            contextEnlargeIn--
            if (contextEnlargeIn == 0) {
                contextEnlargeIn = 2.0.pow(contextNumBits).toInt()
                contextNumBits++
            }
        }

        value = 2
        for (i in 0 until contextNumBits) {
            contextDataVal = (contextDataVal shl 1) or (value and 1)
            if (contextDataPosition == bitsPerChar - 1) {
                contextDataPosition = 0
                contextData.append(getCharFromInt(contextDataVal))
                contextDataVal = 0
            } else {
                contextDataPosition++
            }
            value = value shr 1
        }

        while (true) {
            contextDataVal = contextDataVal shl 1
            if (contextDataPosition == bitsPerChar - 1) {
                contextData.append(getCharFromInt(contextDataVal))
                break
            } else {
                contextDataPosition++
            }
        }
        return contextData.toString()
    }

    fun decompress(compressed: String?): String? {
        if (compressed.isNullOrEmpty()) return null
        return decompress(compressed.length, 32768) { index -> compressed[index].code }
    }

    private fun decompress(
        length: Int,
        resetValue: Int,
        getNextValue: (Int) -> Int
    ): String? {
        val dictionary = mutableMapOf<Int, String>()
        var enLargeIn = 4
        var dictSize = 4
        var numBits = 3
        var bits: Int
        var maxpower: Int
        var power: Int
        var resb: Int
        var entry: String?
        var c: String?
        var w: String?
        val result = StringBuilder()
        val data = Data(getNextValue(0), resetValue, 1)

        for (i in 0..2) {
            dictionary[i] = i.toString()
        }

        bits = 0
        maxpower = 2.0.pow(2).toInt()
        power = 1
        while (power != maxpower) {
            resb = data.value and data.position
            data.position = data.position shr 1
            if (data.position == 0) {
                data.position = resetValue
                data.value = getNextValue(data.index++)
            }
            bits = bits or ((if (resb > 0) 1 else 0) * power)
            power = power shl 1
        }

        val next = bits
        when (next) {
            0 -> {
                bits = 0
                maxpower = 2.0.pow(8).toInt()
                power = 1
                while (power != maxpower) {
                    resb = data.value and data.position
                    data.position = data.position shr 1
                    if (data.position == 0) {
                        data.position = resetValue
                        data.value = getNextValue(data.index++)
                    }
                    bits = bits or ((if (resb > 0) 1 else 0) * power)
                    power = power shl 1
                }
                c = bits.toChar().toString()
            }
            1 -> {
                bits = 0
                maxpower = 2.0.pow(16).toInt()
                power = 1
                while (power != maxpower) {
                    resb = data.value and data.position
                    data.position = data.position shr 1
                    if (data.position == 0) {
                        data.position = resetValue
                        data.value = getNextValue(data.index++)
                    }
                    bits = bits or ((if (resb > 0) 1 else 0) * power)
                    power = power shl 1
                }
                c = bits.toChar().toString()
            }
            2 -> return ""
            else -> return null
        }
        dictionary[3] = c!!
        w = c
        result.append(c)
        while (true) {
            if (data.index > length) return result.toString()
            bits = 0
            maxpower = 2.0.pow(numBits).toInt()
            power = 1
            while (power != maxpower) {
                resb = data.value and data.position
                data.position = data.position shr 1
                if (data.position == 0) {
                    data.position = resetValue
                    data.value = getNextValue(data.index++)
                }
                bits = bits or ((if (resb > 0) 1 else 0) * power)
                power = power shl 1
            }

            val cc = bits
            when (cc) {
                0 -> {
                    bits = 0
                    maxpower = 2.0.pow(8).toInt()
                    power = 1
                    while (power != maxpower) {
                        resb = data.value and data.position
                        data.position = data.position shr 1
                        if (data.position == 0) {
                            data.position = resetValue
                            data.value = getNextValue(data.index++)
                        }
                        bits = bits or ((if (resb > 0) 1 else 0) * power)
                        power = power shl 1
                    }
                    dictionary[dictSize++] = bits.toChar().toString()
                    enLargeIn--
                }
                1 -> {
                    bits = 0
                    maxpower = 2.0.pow(16).toInt()
                    power = 1
                    while (power != maxpower) {
                        resb = data.value and data.position
                        data.position = data.position shr 1
                        if (data.position == 0) {
                            data.position = resetValue
                            data.value = getNextValue(data.index++)
                        }
                        bits = bits or ((if (resb > 0) 1 else 0) * power)
                        power = power shl 1
                    }
                    dictionary[dictSize++] = bits.toChar().toString()
                    enLargeIn--
                }
                2 -> return result.toString()
                else -> {
                    entry = if (cc < dictionary.size && dictionary.containsKey(cc)) {
                        dictionary[cc]
                    } else {
                        if (cc == dictSize) {
                            w + w!![0]
                        } else {
                            return null
                        }
                    }
                    result.append(entry)
                    dictionary[dictSize++] = w + entry!![0]
                    enLargeIn--
                    w = entry
                }
            }

            if (enLargeIn == 0) {
                enLargeIn = 2.0.pow(numBits).toInt()
                numBits++
            }
        }
    }

    private data class Data(var value: Int, var position: Int, var index: Int)
}