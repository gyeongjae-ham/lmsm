package com.lms.core.domain

import com.lms.core.domain.problem.domain.problem.ProblemRatioCalculator
import com.lms.core.domain.problem.domain.problem.exception.InvalidValueException
import com.lms.core.enum.Level
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ProblemRatioCalculatorTest {
    @Test
    fun `상 난이도 선택시 하20퍼센트 중30퍼센트 상50퍼센트로 분배한다`() {
        val calculator = ProblemRatioCalculator()
        val totalProblemCount = 10

        val result = calculator.calculate(Level.HIGH, totalProblemCount)

        Assertions.assertThat(result.lowCount).isEqualTo(2)
        Assertions.assertThat(result.mediumCount).isEqualTo(3)
        Assertions.assertThat(result.highCount).isEqualTo(5)
    }

    @Test
    fun `중 난이도 선택시 하25퍼센트 중50퍼센트 상25퍼센트로 분배한다`() {
        val calculator = ProblemRatioCalculator()
        val totalProblemCount = 10

        val result = calculator.calculate(Level.MEDIUM, totalProblemCount)

        Assertions.assertThat(result.lowCount).isEqualTo(2)
        Assertions.assertThat(result.mediumCount).isEqualTo(5)
        Assertions.assertThat(result.highCount).isEqualTo(3)
    }

    @Test
    fun `하 난이도 선택시 하50퍼센트 중30퍼센트 상20퍼센트로 분배한다`() {
        val calculator = ProblemRatioCalculator()
        val totalProblemCount = 10

        val result = calculator.calculate(Level.LOW, totalProblemCount)

        Assertions.assertThat(result.lowCount).isEqualTo(5)
        Assertions.assertThat(result.mediumCount).isEqualTo(3)
        Assertions.assertThat(result.highCount).isEqualTo(2)
    }

    @Test
    fun `총 문제 수가 홀수일 때 반올림으로 인한 초과분을 각 level의 최소 비율에서 차감한다`() {
        val calculator = ProblemRatioCalculator()
        val totalProblemCount = 9
        val result = calculator.calculate(Level.HIGH, totalProblemCount)

        Assertions.assertThat(result.lowCount).isEqualTo(1)
        Assertions.assertThat(result.mediumCount).isEqualTo(3)
        Assertions.assertThat(result.highCount).isEqualTo(5)
        Assertions.assertThat(result.lowCount + result.mediumCount + result.highCount).isEqualTo(9)
    }

    @Test
    fun `총 문제 수가 1일 때 정상 동작한다`() {
        val calculator = ProblemRatioCalculator()
        val result = calculator.calculate(Level.HIGH, 1)

        Assertions.assertThat(result.lowCount + result.mediumCount + result.highCount).isEqualTo(1)
    }

    @Test
    fun `총 문제 수가 0일 때 Exception이 발생한다`() {
        val calculator = ProblemRatioCalculator()
        Assertions.assertThatThrownBy { calculator.calculate(Level.HIGH, 0) }
            .isInstanceOf(InvalidValueException::class.java)
            .hasMessage("Invalid value exception")
    }

    @Test
    fun `총 문제 수가 음수일 때 Exception이 발생한다`() {
        val calculator = ProblemRatioCalculator()
        Assertions.assertThatThrownBy { calculator.calculate(Level.HIGH, -1) }
            .isInstanceOf(InvalidValueException::class.java)
            .hasMessage("Invalid value exception")
    }

    @Test
    fun `총 문제 수가 큰 수일 때 비율이 정확히 계산된다`() {
        val calculator = ProblemRatioCalculator()
        val totalProblemCount = 100
        val result = calculator.calculate(Level.HIGH, totalProblemCount)

        Assertions.assertThat(result.lowCount).isEqualTo(20)
        Assertions.assertThat(result.mediumCount).isEqualTo(30)
        Assertions.assertThat(result.highCount).isEqualTo(50)
    }

    @ParameterizedTest
    @CsvSource(
        "LOW, 13",
        "MEDIUM, 17",
        "HIGH, 23"
    )
    fun `모든 레벨에서 총합이 입력값과 같거나 작다`(level: Level, totalCount: Int) {
        val calculator = ProblemRatioCalculator()
        val result = calculator.calculate(level, totalCount)

        val sum = result.lowCount + result.mediumCount + result.highCount
        Assertions.assertThat(sum).isLessThanOrEqualTo(totalCount)
    }
}
