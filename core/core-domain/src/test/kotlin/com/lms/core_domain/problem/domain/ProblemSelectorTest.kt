package com.lms.core_domain.problem.domain

import com.lms.core_common.enum.ProblemType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProblemSelectorTest {
    @Test
    fun `RatioResult와 Problem List로 Problems를 생성한다`() {
        val ratioResult = RatioCalculateResult()
        val problemList = listOf(
            Problem(
                unitCode = "testCode",
                level = 2,
                problemType = ProblemType.SELECTION,
                answer = "1"
            )
        )
        val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = problemList)
        val otherProblemSelector = ProblemSelector(
            ratioResult = RatioCalculateResult(), problems = listOf(
                Problem(
                    unitCode = "testCode",
                    level = 2,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                )
            )
        )
        assertThat(problemSelector).isEqualTo(otherProblemSelector)
    }

    @Test
    fun `빈 문제 리스트로 Problems를 생성할 수 있다`() {
        val ratioResult = RatioCalculateResult()
        val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = emptyList())

        assertThat(problemSelector.extract()).isEmpty()
    }

    @Test
    fun `extract 메서드는 문제 리스트를 반환한다`() {
        val ratioResult = RatioCalculateResult(lowCount = 2, mediumCount = 1, highCount = 1)
        val problemList = listOf(
            Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "2"),
            Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "3"),
            Problem(unitCode = "test", level = 2, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(unitCode = "test", level = 2, problemType = ProblemType.SELECTION, answer = "2"),
            Problem(unitCode = "test", level = 3, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(unitCode = "test", level = 3, problemType = ProblemType.SELECTION, answer = "2")
        )
        val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = problemList)

        val extracted = problemSelector.extract()
        assertThat(extracted).hasSize(3)
    }

    @Nested
    inner class LevelBasedExtractionTest {

        @Test
        fun `LOW 레벨 문제만 있을 때 정상적으로 추출된다`() {
            val ratioResult = RatioCalculateResult(lowCount = 2, mediumCount = 0, highCount = 0)
            val problemList = listOf(
                Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
                Problem(unitCode = "test", level = 1, problemType = ProblemType.SUBJECTIVE, answer = "2"),
                Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "3")
            )
            val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = problemList)

            val extracted = problemSelector.extract()
            assertThat(extracted).hasSize(2)
            assertThat(extracted).allMatch { it.level == 1 }
        }

        @Test
        fun `MEDIUM 레벨 문제만 있을 때 정상적으로 추출된다`() {
            val ratioResult = RatioCalculateResult(lowCount = 0, mediumCount = 2, highCount = 0)
            val problemList = listOf(
                Problem(unitCode = "test", level = 2, problemType = ProblemType.SELECTION, answer = "1"),
                Problem(unitCode = "test", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2"),
                Problem(unitCode = "test", level = 2, problemType = ProblemType.SELECTION, answer = "3")
            )
            val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = problemList)

            val extracted = problemSelector.extract()
            assertThat(extracted).hasSize(2)
            assertThat(extracted).allMatch { it.level == 2 }
        }

        @Test
        fun `HIGH 레벨 문제만 있을 때 정상적으로 추출된다`() {
            val ratioResult = RatioCalculateResult(lowCount = 0, mediumCount = 0, highCount = 2)
            val problemList = listOf(
                Problem(unitCode = "test", level = 5, problemType = ProblemType.SELECTION, answer = "1"),
                Problem(unitCode = "test", level = 5, problemType = ProblemType.SUBJECTIVE, answer = "2"),
                Problem(unitCode = "test", level = 5, problemType = ProblemType.SELECTION, answer = "3")
            )
            val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = problemList)

            val extracted = problemSelector.extract()
            assertThat(extracted).hasSize(2)
            assertThat(extracted).allMatch { it.level == 5 }
        }
    }

    @Test
    fun `요청된 개수보다 적은 문제가 있을 때 있는 만큼만 추출된다`() {
        val ratioResult = RatioCalculateResult(lowCount = 5, mediumCount = 0, highCount = 0)
        val problemList = listOf(
            Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "2")
        )
        val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = problemList)

        val extracted = problemSelector.extract()
        assertThat(extracted).hasSize(2)
    }

    @Test
    fun `다양한 레벨의 문제가 섞여 있을 때 올바른 비율로 추출된다`() {
        val ratioResult = RatioCalculateResult(lowCount = 2, mediumCount = 1, highCount = 1)
        val problemList = listOf(
            Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(unitCode = "test", level = 1, problemType = ProblemType.SUBJECTIVE, answer = "2"),
            Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "3"),

            Problem(unitCode = "test", level = 2, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(unitCode = "test", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2"),

            Problem(unitCode = "test", level = 5, problemType = ProblemType.SELECTION, answer = "1"),
            Problem(unitCode = "test", level = 5, problemType = ProblemType.SUBJECTIVE, answer = "2")
        )
        val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = problemList)

        val extracted = problemSelector.extract()
        assertThat(extracted).hasSize(4)

        val levelCounts = extracted.groupingBy { it.level }.eachCount()
        assertThat(levelCounts[1]).isEqualTo(2)
        assertThat(levelCounts[2]).isEqualTo(1)
        assertThat(levelCounts[5]).isEqualTo(1)
    }

    @Nested
    inner class EqualsAndHashCodeTest {

        @Test
        fun `같은 데이터로 생성된 Problems는 equals에서 true를 반환한다`() {
            val ratioResult = RatioCalculateResult(lowCount = 1, mediumCount = 1, highCount = 1)
            val problemList = listOf(
                Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
                Problem(unitCode = "test", level = 2, problemType = ProblemType.SELECTION, answer = "2"),
                Problem(unitCode = "test", level = 3, problemType = ProblemType.SELECTION, answer = "3")
            )

            val problemSelector1 = ProblemSelector(ratioResult = ratioResult, problems = problemList)
            val problemSelector2 = ProblemSelector(ratioResult = ratioResult, problems = problemList)

            assertThat(problemSelector1).isEqualTo(problemSelector2)
        }

        @Test
        fun `다른 데이터로 생성된 Problems는 equals에서 false를 반환한다`() {
            val ratioResult1 = RatioCalculateResult(lowCount = 1, mediumCount = 0, highCount = 0)
            val ratioResult2 = RatioCalculateResult(lowCount = 2, mediumCount = 0, highCount = 0)
            val problemList = listOf(
                Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
                Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "2")
            )

            val problemSelector1 = ProblemSelector(ratioResult = ratioResult1, problems = problemList)
            val problemSelector2 = ProblemSelector(ratioResult = ratioResult2, problems = problemList)

            assertThat(problemSelector1).isNotEqualTo(problemSelector2)
        }

        @Test
        fun `자기 자신과 비교할 때 equals에서 true를 반환한다`() {
            val ratioResult = RatioCalculateResult()
            val problemList = listOf(
                Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "1")
            )
            val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = problemList)

            assertThat(problemSelector).isEqualTo(problemSelector)
        }

        @Test
        fun `null과 비교할 때 equals에서 false를 반환한다`() {
            val ratioResult = RatioCalculateResult()
            val problemList = listOf(
                Problem(unitCode = "test", level = 1, problemType = ProblemType.SELECTION, answer = "1")
            )
            val problemSelector = ProblemSelector(ratioResult = ratioResult, problems = problemList)

            assertThat(problemSelector).isNotEqualTo(null)
        }
    }
}
