package com.lms.db.core.piece

import com.lms.core_common.enum.ProblemType
import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.ProblemWithSequence
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems
import com.lms.db.core.BaseRepositoryTest
import com.lms.db.core.problem.ProblemEntity
import com.lms.db.core.problem.ProblemJpaRepository
import com.lms.db.core.problem.ProblemRepositoryImpl
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PieceRepositoryImplTest : BaseRepositoryTest() {

    @Autowired
    private lateinit var pieceJpaRepository: PieceJpaRepository

    @Autowired
    private lateinit var pieceProblemJpaRepository: PieceProblemJpaRepository

    @Autowired
    private lateinit var problemJpaRepository: ProblemJpaRepository

    private lateinit var problemRepositoryImpl: ProblemRepositoryImpl

    private lateinit var pieceRepository: PieceRepositoryImpl

    @BeforeEach
    fun setup() {
        problemRepositoryImpl = ProblemRepositoryImpl(problemJpaRepository)
        pieceRepository = PieceRepositoryImpl(pieceJpaRepository, pieceProblemJpaRepository, problemRepositoryImpl)

        pieceProblemJpaRepository.deleteAll()
        pieceJpaRepository.deleteAll()
        problemJpaRepository.deleteAll()
    }

    @Test
    fun `Piece 저장 시 PieceEntity와 PieceProblemEntity가 모두 저장된다`() {
        val problemEntities = listOf(
            ProblemEntity(unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            ProblemEntity(unitCode = "uc1580", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2"),
            ProblemEntity(unitCode = "uc1583", level = 3, problemType = ProblemType.SELECTION, answer = "3")
        )
        val savedProblems = problemJpaRepository.saveAll(problemEntities)

        val problems = listOf(
            Problem(Problem.ProblemId(savedProblems[0].id), "uc1580", 1, ProblemType.SELECTION, "1"),
            Problem(Problem.ProblemId(savedProblems[1].id), "uc1580", 2, ProblemType.SUBJECTIVE, "2"),
            Problem(Problem.ProblemId(savedProblems[2].id), "uc1583", 3, ProblemType.SELECTION, "3")
        )
        val piece = Piece(
            name = "테스트 학습지",
            teacherId = 1L,
            problems = Problems(problems)
        )

        val savedPiece = pieceRepository.savePiece(piece)

        val pieceEntities = pieceJpaRepository.findAll()
        assertThat(pieceEntities).hasSize(1)
        assertThat(pieceEntities[0].name).isEqualTo("테스트 학습지")
        assertThat(pieceEntities[0].teacherId).isEqualTo(1L)

        val pieceProblemEntities = pieceProblemJpaRepository.findAll()
        assertThat(pieceProblemEntities).hasSize(3)

        // sequence 순서 확인 (정렬된 순서: uc1580 level1, uc1580 level2, uc1583 level3)
        val sortedPieceProblems = pieceProblemEntities.sortedBy { it.sequence }
        assertThat(sortedPieceProblems[0].problemId).isEqualTo(savedProblems[0].id)
        assertThat(sortedPieceProblems[0].sequence).isEqualTo(10)

        assertThat(sortedPieceProblems[1].problemId).isEqualTo(savedProblems[1].id)
        assertThat(sortedPieceProblems[1].sequence).isEqualTo(20)

        assertThat(sortedPieceProblems[2].problemId).isEqualTo(savedProblems[2].id)
        assertThat(sortedPieceProblems[2].sequence).isEqualTo(30)

        assertThat(savedPiece.getName()).isEqualTo("테스트 학습지")
        assertThat(savedPiece.getTeacherId()).isEqualTo(1L)
        assertThat(savedPiece.getProblemCount()).isEqualTo(3)
    }

    @Test
    fun `정렬되지 않은 문제들이 unit_code와 level 순으로 정렬되어 저장된다`() {
        val problemEntities = listOf(
            ProblemEntity(unitCode = "uc1583", level = 3, problemType = ProblemType.SELECTION, answer = "3"),
            ProblemEntity(unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            ProblemEntity(unitCode = "uc1580", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2")
        )
        val savedProblems = problemJpaRepository.saveAll(problemEntities)

        val problems = listOf(
            Problem(Problem.ProblemId(savedProblems[0].id), "uc1583", 3, ProblemType.SELECTION, "3"),
            Problem(Problem.ProblemId(savedProblems[1].id), "uc1580", 1, ProblemType.SELECTION, "1"),
            Problem(Problem.ProblemId(savedProblems[2].id), "uc1580", 2, ProblemType.SUBJECTIVE, "2")
        )
        val piece = Piece(
            name = "정렬 테스트 학습지",
            teacherId = 1L,
            problems = Problems(problems)
        )

        pieceRepository.savePiece(piece)

        val pieceProblemEntities = pieceProblemJpaRepository.findAll().sortedBy { it.sequence }

        // 정렬된 순서 확인: uc1580(level1), uc1580(level2), uc1583(level3)
        assertThat(pieceProblemEntities[0].problemId).isEqualTo(savedProblems[1].id) // uc1580, level1
        assertThat(pieceProblemEntities[0].sequence).isEqualTo(10)

        assertThat(pieceProblemEntities[1].problemId).isEqualTo(savedProblems[2].id) // uc1580, level2
        assertThat(pieceProblemEntities[1].sequence).isEqualTo(20)

        assertThat(pieceProblemEntities[2].problemId).isEqualTo(savedProblems[0].id) // uc1583, level3
        assertThat(pieceProblemEntities[2].sequence).isEqualTo(30)
    }

    @Test
    fun `단일 문제로 Piece 저장 시 sequence가 10으로 설정된다`() {
        val problemEntity = ProblemEntity(unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1")
        val savedProblem = problemJpaRepository.save(problemEntity)

        val problems = listOf(
            Problem(Problem.ProblemId(savedProblem.id), "uc1580", 1, ProblemType.SELECTION, "1")
        )
        val piece = Piece(
            name = "단일 문제 학습지",
            teacherId = 1L,
            problems = Problems(problems)
        )

        pieceRepository.savePiece(piece)

        val pieceProblemEntities = pieceProblemJpaRepository.findAll()
        assertThat(pieceProblemEntities).hasSize(1)
        assertThat(pieceProblemEntities[0].sequence).isEqualTo(10)
    }

    @Test
    fun `Piece 저장 후 반환된 객체는 올바른 정보를 포함한다`() {
        val problemEntities = listOf(
            ProblemEntity(unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            ProblemEntity(unitCode = "uc1580", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2")
        )
        val savedProblems = problemJpaRepository.saveAll(problemEntities)

        val problems = listOf(
            Problem(Problem.ProblemId(savedProblems[0].id), "uc1580", 1, ProblemType.SELECTION, "1"),
            Problem(Problem.ProblemId(savedProblems[1].id), "uc1580", 2, ProblemType.SUBJECTIVE, "2")
        )
        val piece = Piece(
            name = "반환 테스트 학습지",
            teacherId = 123L,
            problems = Problems(problems)
        )

        val savedPiece = pieceRepository.savePiece(piece)

        assertThat(savedPiece.getName()).isEqualTo("반환 테스트 학습지")
        assertThat(savedPiece.getTeacherId()).isEqualTo(123L)
        assertThat(savedPiece.getProblemCount()).isEqualTo(2)

        val problemsWithSequence = savedPiece.getProblemsWithSequence()
        assertThat(problemsWithSequence).hasSize(2)
        assertThat(problemsWithSequence[0].sequence).isEqualTo(10)
        assertThat(problemsWithSequence[1].sequence).isEqualTo(20)
    }

    @Test
    fun `존재하는 Piece ID로 조회 시 정상적으로 반환된다`() {
        val problemEntities = listOf(
            ProblemEntity(unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            ProblemEntity(unitCode = "uc1580", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2")
        )
        val savedProblems = problemJpaRepository.saveAll(problemEntities)

        val problems = listOf(
            Problem(Problem.ProblemId(savedProblems[0].id), "uc1580", 1, ProblemType.SELECTION, "1"),
            Problem(Problem.ProblemId(savedProblems[1].id), "uc1580", 2, ProblemType.SUBJECTIVE, "2")
        )
        val piece = Piece(
            name = "조회 테스트 학습지",
            teacherId = 1L,
            problems = Problems(problems)
        )

        val savedPiece = pieceRepository.savePiece(piece)

        val foundPiece = pieceRepository.findById(savedPiece.id)

        assertThat(foundPiece.id).isEqualTo(savedPiece.id)
        assertThat(foundPiece.getName()).isEqualTo("조회 테스트 학습지")
        assertThat(foundPiece.getTeacherId()).isEqualTo(1L)
        assertThat(foundPiece.getProblemCount()).isEqualTo(2)

        val problemsWithSequence = foundPiece.getProblemsWithSequence()
        assertThat(problemsWithSequence).hasSize(2)
        assertThat(problemsWithSequence[0].sequence).isEqualTo(10)
        assertThat(problemsWithSequence[1].sequence).isEqualTo(20)
    }

    @Test
    fun `존재하지 않는 Piece ID로 조회 시 BusinessException이 발생한다`() {
        val nonExistentPieceId = Piece.PieceId(999L)

        assertThatThrownBy { pieceRepository.findById(nonExistentPieceId) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Piece not found")
    }

    @Test
    fun `Piece 업데이트 시 PieceProblem 엔티티가 삭제되고 재생성된다`() {
        val problemEntities = listOf(
            ProblemEntity(unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            ProblemEntity(unitCode = "uc1580", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2"),
            ProblemEntity(unitCode = "uc1583", level = 3, problemType = ProblemType.SELECTION, answer = "3")
        )
        val savedProblems = problemJpaRepository.saveAll(problemEntities)

        val problems = listOf(
            Problem(Problem.ProblemId(savedProblems[0].id), "uc1580", 1, ProblemType.SELECTION, "1"),
            Problem(Problem.ProblemId(savedProblems[1].id), "uc1580", 2, ProblemType.SUBJECTIVE, "2")
        )
        val piece = Piece(
            name = "업데이트 테스트 학습지",
            teacherId = 1L,
            problems = Problems(problems)
        )

        val savedPiece = pieceRepository.savePiece(piece)

        val originalPieceProblems = pieceProblemJpaRepository.findByPieceIdOrderBySequence(savedPiece.id.value)
        assertThat(originalPieceProblems).hasSize(2)

        val updatedProblemsWithSequence = listOf(
            ProblemWithSequence(
                problem = Problem(Problem.ProblemId(savedProblems[2].id), "uc1583", 3, ProblemType.SELECTION, "3"),
                sequence = 5
            ),
            ProblemWithSequence(
                problem = Problem(Problem.ProblemId(savedProblems[0].id), "uc1580", 1, ProblemType.SELECTION, "1"),
                sequence = 10
            ),
            ProblemWithSequence(
                problem = Problem(Problem.ProblemId(savedProblems[1].id), "uc1580", 2, ProblemType.SUBJECTIVE, "2"),
                sequence = 20
            )
        )

        val updatedPiece = Piece(
            pieceId = savedPiece.id,
            name = "업데이트 테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = updatedProblemsWithSequence
        )

        pieceRepository.savePiece(updatedPiece)

        val updatedPieceProblems = pieceProblemJpaRepository.findByPieceIdOrderBySequence(savedPiece.id.value)
        assertThat(updatedPieceProblems).hasSize(3)
        assertThat(updatedPieceProblems[0].sequence).isEqualTo(5)
        assertThat(updatedPieceProblems[0].problemId).isEqualTo(savedProblems[2].id)
        assertThat(updatedPieceProblems[1].sequence).isEqualTo(10)
        assertThat(updatedPieceProblems[1].problemId).isEqualTo(savedProblems[0].id)
        assertThat(updatedPieceProblems[2].sequence).isEqualTo(20)
        assertThat(updatedPieceProblems[2].problemId).isEqualTo(savedProblems[1].id)
    }

    @Test
    fun `조회된 Piece의 문제들이 sequence 순서로 정렬되어 반환된다`() {
        val problemEntities = listOf(
            ProblemEntity(unitCode = "uc1580", level = 1, problemType = ProblemType.SELECTION, answer = "1"),
            ProblemEntity(unitCode = "uc1580", level = 2, problemType = ProblemType.SUBJECTIVE, answer = "2"),
            ProblemEntity(unitCode = "uc1583", level = 3, problemType = ProblemType.SELECTION, answer = "3")
        )
        val savedProblems = problemJpaRepository.saveAll(problemEntities)

        val problems = listOf(
            Problem(Problem.ProblemId(savedProblems[2].id), "uc1583", 3, ProblemType.SELECTION, "3"),
            Problem(Problem.ProblemId(savedProblems[0].id), "uc1580", 1, ProblemType.SELECTION, "1"),
            Problem(Problem.ProblemId(savedProblems[1].id), "uc1580", 2, ProblemType.SUBJECTIVE, "2")
        )

        val piece = Piece(
            name = "정렬 테스트 학습지",
            teacherId = 1L,
            problems = Problems(problems)
        )

        val savedPiece = pieceRepository.savePiece(piece)
        val foundPiece = pieceRepository.findById(savedPiece.id)

        val problemsWithSequence = foundPiece.getProblemsWithSequence()
        assertThat(problemsWithSequence).hasSize(3)

        // 정렬된 순서로 반환되는지 확인: uc1580(level1), uc1580(level2), uc1583(level3)
        assertThat(problemsWithSequence[0].sequence).isEqualTo(10)
        assertThat(problemsWithSequence[0].problem.id.value).isEqualTo(savedProblems[0].id) // uc1580, level1

        assertThat(problemsWithSequence[1].sequence).isEqualTo(20)
        assertThat(problemsWithSequence[1].problem.id.value).isEqualTo(savedProblems[1].id) // uc1580, level2

        assertThat(problemsWithSequence[2].sequence).isEqualTo(30)
        assertThat(problemsWithSequence[2].problem.id.value).isEqualTo(savedProblems[2].id) // uc1583, level3
    }
}
