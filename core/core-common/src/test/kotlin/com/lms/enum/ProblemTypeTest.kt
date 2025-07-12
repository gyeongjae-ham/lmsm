package com.lms.enum

import com.lms.core.enum.ProblemType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProblemTypeTest {
    @Test
    fun `ProblemType이 같은 것끼리 비교하면 true를 반환한다`() {
        val allType = ProblemType.ALL
        val result = allType.isSame(ProblemType.ALL)

        assertThat(result).isTrue()
    }

    @Test
    fun `ProblemType이 다른 것끼리 비교하면 false를 반환한다`() {
        val allType = ProblemType.ALL
        val result = allType.isSame(ProblemType.SELECTION)

        assertThat(result).isFalse()
    }
}
