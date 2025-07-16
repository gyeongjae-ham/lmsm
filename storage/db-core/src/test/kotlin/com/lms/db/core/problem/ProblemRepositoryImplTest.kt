package com.lms.db.core.problem

import com.lms.core_common.enum.ProblemType
import com.lms.db.core.BaseRepositoryTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ProblemRepositoryImplTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var problemJpaRepository: ProblemJpaRepository
    private lateinit var problemRepository: ProblemRepositoryImpl

    @BeforeEach
    fun setup() {
        problemRepository = ProblemRepositoryImpl(problemJpaRepository)
        problemJpaRepository.deleteAll()

        val problems = listOf(
            ProblemEntity(unitCode = "A01", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            ProblemEntity(unitCode = "A01", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2"),
            ProblemEntity(unitCode = "B01", level = 3, problemType = ProblemType.SELECTION, answer = "3"),
            ProblemEntity(unitCode = "C01", level = 4, problemType = ProblemType.SELECTION, answer = "4")
        )
        problemJpaRepository.saveAll(problems)
    }

    @Test
    fun `유형 코드 리스트로 문제 조회 시, 해당 코드의 문제들을 반환한다`() {
        val unitCodeList = listOf("A01", "C01")
        val result = problemRepository.getWithUnitCodeList(unitCodeList)

        assertThat(result).hasSize(3)
        assertThat(result.map { it.unitCode }).containsExactlyInAnyOrder("A01", "A01", "C01")
    }

    @Test
    fun `유형 코드 리스트와 문제 타입으로 조회 시, 모든 조건에 맞는 문제들을 반환한다`() {
        val unitCodeList = listOf("A01", "B01")
        val problemType = ProblemType.SELECTION

        val result = problemRepository.getWithUnitCodeListAndType(unitCodeList, problemType)

        assertThat(result).hasSize(2)
        assertThat(result.map { it.unitCode }).containsExactlyInAnyOrder("A01", "B01")
        assertThat(result.all { it.problemType == ProblemType.SELECTION }).isTrue()
    }

    @Test
    fun `유형 코드 리스트와 문제 타입 ALL로 조회 시, 빈 리스트를 반환한다`() {
        val unitCodeList = listOf("A01", "B01")
        val problemType = ProblemType.ALL

        val result = problemRepository.getWithUnitCodeListAndType(unitCodeList, problemType)

        assertThat(result).isEmpty()
    }
}
