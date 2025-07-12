package com.lms.enum

import com.lms.enum.exception.InvalidLevelValueException

private const val MAX_LEVEL_VALUE = 5
private const val MIN_LEVEL_VALUE = 1
private val LOW_NUMBER = listOf(1)
private val MEDIUM_NUMBER = listOf(2, 3, 4)
private val HIGH_NUMBER = listOf(5)

enum class Level {
    HIGH,
    MEDIUM,
    LOW;

    fun toInt(): List<Int> {
        return when (this) {
            LOW -> LOW_NUMBER
            MEDIUM -> MEDIUM_NUMBER
            HIGH -> HIGH_NUMBER
        }
    }

    companion object {
        fun fromInt(levelValue: Int): Level {
            if (levelValue > MAX_LEVEL_VALUE || levelValue < MIN_LEVEL_VALUE) {
                throw InvalidLevelValueException(levelValue)
            }

            return when (levelValue) {
                1 -> LOW
                2, 3, 4 -> MEDIUM
                5 -> HIGH
                else -> MEDIUM
            }
        }
    }
}
