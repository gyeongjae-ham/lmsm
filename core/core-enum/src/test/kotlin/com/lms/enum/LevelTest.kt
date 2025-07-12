package com.lms.enum

import com.lms.enum.exception.InvalidLevelValueException
import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class LevelTest {
    @ParameterizedTest
    @CsvSource(value = ["1", "2", "3", "4", "5"])
    fun `난이도 정수형을 Level Enum 값으로 변환한다`(levelValue: Int) {
        val level = Level.fromInt(levelValue)

        when (levelValue) {
            1 -> assertEquals(Level.LOW, level)
            2, 3, 4 -> assertEquals(Level.MEDIUM, level)
            5 -> assertEquals(Level.HIGH, level)
        }
    }

    @ParameterizedTest
    @CsvSource(value = ["0", "6", "7"])
    fun `난이도 정수형값이 1~5 사이의 값이 아니라면 Exception이 발생한다`(levelValue: Int) {
        Assertions.assertThatThrownBy { Level.fromInt(levelValue) }
            .isInstanceOf(InvalidLevelValueException::class.java)
            .hasMessage("Invalid level value $levelValue")
    }

    @ParameterizedTest
    @CsvSource(value = ["HIGH", "MEDIUM", "LOW"])
    fun `Level enum 값을 받아서 정수형 리스트를 반환한다`(level: Level) {
        val levelValueList = level.toInt()
        when (level) {
            Level.LOW -> assertEquals(listOf(1), levelValueList)
            Level.MEDIUM -> assertEquals(listOf(2, 3, 4), levelValueList)
            Level.HIGH -> assertEquals(listOf(5), levelValueList)
        }
    }
}
