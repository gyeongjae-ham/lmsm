package com.lms.core_domain.piece.service

import com.lms.core_common.enum.ProblemType
import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.ProblemWithSequence
import com.lms.core_domain.piece.domain.request.PieceAssignRequest
import com.lms.core_domain.piece.domain.request.PieceCreateRequest
import com.lms.core_domain.piece.domain.request.ProblemOrderUpdateRequest
import com.lms.core_domain.problem.domain.Problem
import com.lms.core_domain.problem.domain.Problems
import com.lms.core_domain.problem.service.ProblemFinder
import com.lms.core_domain.studentanswer.service.StudentAnswerFinder
import com.lms.core_domain.studentanswer.service.StudentAnswerSaver
import com.lms.core_domain.studentanswer.service.StudentAnswerScorer
import com.lms.core_domain.studentpiece.domain.StudentPiece
import com.lms.core_domain.studentpiece.service.StudentPieceFinder
import com.lms.core_domain.studentpiece.service.StudentPieceSaver
import com.lms.core_domain.user.domain.User
import com.lms.core_domain.user.service.UserFinder
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PieceServiceTest {

    @MockK
    private lateinit var problemFinder: ProblemFinder

    @MockK
    private lateinit var pieceSaver: PieceSaver

    @MockK
    private lateinit var pieceFinder: PieceFinder

    @MockK
    private lateinit var userFinder: UserFinder

    @MockK
    private lateinit var studentPieceFinder: StudentPieceFinder

    @MockK
    private lateinit var studentPieceSaver: StudentPieceSaver

    @MockK
    private lateinit var studentAnswerScorer: StudentAnswerScorer

    @MockK
    private lateinit var studentAnswerSaver: StudentAnswerSaver

    @MockK
    private lateinit var studentAnswerFinder: StudentAnswerFinder

    private lateinit var pieceService: PieceService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        pieceService = PieceService(
            problemFinder,
            pieceSaver,
            pieceFinder,
            userFinder,
            studentPieceFinder,
            studentPieceSaver,
            studentAnswerScorer,
            studentAnswerSaver,
            studentAnswerFinder
        )
    }

    @Test
    fun `학습지 생성 요청이 성공적으로 처리된다`() {
        val request = PieceCreateRequest(
            name = "테스트 학습지",
            teacherId = 1L,
            problemIds = listOf(1L, 2L)
        )
        val problems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "2"
                )
            )
        )
        val piece = Piece(
            name = "테스트 학습지",
            teacherId = 1L,
            problems = problems
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(1L),
            name = "테스트 학습지",
            teacherId = 1L,
            problems = problems
        )

        every { problemFinder.getProblemsForPiece(request.problemIds) } returns problems
        every { pieceSaver.savePiece(any()) } returns savedPiece

        val result = pieceService.create(request)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.name).isEqualTo("테스트 학습지")
        assertThat(result.teacherId).isEqualTo(1L)
        assertThat(result.problemCount).isEqualTo(2)
        verify { problemFinder.getProblemsForPiece(request.problemIds) }
        verify { pieceSaver.savePiece(any()) }
    }

    @Test
    fun `단일 문제로 학습지를 생성한다`() {
        val request = PieceCreateRequest(
            name = "단일 문제 학습지",
            teacherId = 2L,
            problemIds = listOf(1L)
        )
        val problems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                )
            )
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(2L),
            name = "단일 문제 학습지",
            teacherId = 2L,
            problems = problems
        )

        every { problemFinder.getProblemsForPiece(request.problemIds) } returns problems
        every { pieceSaver.savePiece(any()) } returns savedPiece

        val result = pieceService.create(request)

        assertThat(result.id).isEqualTo(2L)
        assertThat(result.name).isEqualTo("단일 문제 학습지")
        assertThat(result.teacherId).isEqualTo(2L)
        assertThat(result.problemCount).isEqualTo(1)
    }

    @Test
    fun `여러 문제로 학습지를 생성한다`() {
        val request = PieceCreateRequest(
            name = "다중 문제 학습지",
            teacherId = 3L,
            problemIds = listOf(1L, 2L, 3L, 4L, 5L)
        )
        val problems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "2"
                ),
                Problem(
                    problemId = Problem.ProblemId(3L),
                    unitCode = "uc1583",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "3"
                ),
                Problem(
                    problemId = Problem.ProblemId(4L),
                    unitCode = "uc1583",
                    level = 2,
                    problemType = ProblemType.SUBJECTIVE,
                    answer = "4"
                ),
                Problem(
                    problemId = Problem.ProblemId(5L),
                    unitCode = "uc1585",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "5"
                )
            )
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(3L),
            name = "다중 문제 학습지",
            teacherId = 3L,
            problems = problems
        )

        every { problemFinder.getProblemsForPiece(request.problemIds) } returns problems
        every { pieceSaver.savePiece(any()) } returns savedPiece

        val result = pieceService.create(request)

        assertThat(result.id).isEqualTo(3L)
        assertThat(result.name).isEqualTo("다중 문제 학습지")
        assertThat(result.teacherId).isEqualTo(3L)
        assertThat(result.problemCount).isEqualTo(5)
    }

    @Test
    fun `서비스 호출 순서가 올바르다`() {
        val request = PieceCreateRequest(
            name = "순서 검증 학습지",
            teacherId = 4L,
            problemIds = listOf(1L)
        )
        val problems = Problems(
            listOf(
                Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                )
            )
        )
        val savedPiece = Piece(
            pieceId = Piece.PieceId(4L),
            name = "순서 검증 학습지",
            teacherId = 4L,
            problems = problems
        )

        every { problemFinder.getProblemsForPiece(request.problemIds) } returns problems
        every { pieceSaver.savePiece(any()) } returns savedPiece

        pieceService.create(request)

        verify(exactly = 1) { problemFinder.getProblemsForPiece(request.problemIds) }
        verify(exactly = 1) { pieceSaver.savePiece(any()) }
    }

    @Test
    fun `문제 순서 변경이 성공적으로 처리된다`() {
        val pieceId = Piece.PieceId(1L)
        val request = ProblemOrderUpdateRequest(
            problemId = 2L,
            targetPosition = 0
        )

        val originalProblems = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 10
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SELECTION,
                    answer = "2"
                ),
                sequence = 20
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(3L),
                    unitCode = "uc1580",
                    level = 3,
                    problemType = ProblemType.SELECTION,
                    answer = "3"
                ),
                sequence = 30
            )
        )

        val originalPiece = Piece(
            pieceId = pieceId,
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = originalProblems
        )

        val reorderedProblems = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SELECTION,
                    answer = "2"
                ),
                sequence = 5
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 10
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(3L),
                    unitCode = "uc1580",
                    level = 3,
                    problemType = ProblemType.SELECTION,
                    answer = "3"
                ),
                sequence = 30
            )
        )

        val updatedPiece = Piece(
            pieceId = pieceId,
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = reorderedProblems
        )

        every { pieceFinder.getWithId(pieceId) } returns originalPiece
        every { pieceSaver.savePiece(any()) } returns updatedPiece

        val result = pieceService.updateProblemOrder(pieceId, request)

        assertThat(result.pieceId).isEqualTo(1L)
        assertThat(result.name).isEqualTo("테스트 학습지")
        assertThat(result.problemCount).isEqualTo(3)
        assertThat(result.problems).hasSize(3)
        assertThat(result.problems[0].problemId).isEqualTo(2L)
        assertThat(result.problems[1].problemId).isEqualTo(1L)
        assertThat(result.problems[2].problemId).isEqualTo(3L)

        verify { pieceFinder.getWithId(pieceId) }
        verify { pieceSaver.savePiece(any()) }
    }

    @Test
    fun `문제를 맨 뒤로 이동시킨다`() {
        val pieceId = Piece.PieceId(1L)
        val request = ProblemOrderUpdateRequest(
            problemId = 1L,
            targetPosition = 2
        )

        val originalProblems = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 10
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SELECTION,
                    answer = "2"
                ),
                sequence = 20
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(3L),
                    unitCode = "uc1580",
                    level = 3,
                    problemType = ProblemType.SELECTION,
                    answer = "3"
                ),
                sequence = 30
            )
        )

        val originalPiece = Piece(
            pieceId = pieceId,
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = originalProblems
        )

        val reorderedProblems = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SELECTION,
                    answer = "2"
                ),
                sequence = 20
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(3L),
                    unitCode = "uc1580",
                    level = 3,
                    problemType = ProblemType.SELECTION,
                    answer = "3"
                ),
                sequence = 30
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 40
            )
        )

        val updatedPiece = Piece(
            pieceId = pieceId,
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = reorderedProblems
        )

        every { pieceFinder.getWithId(pieceId) } returns originalPiece
        every { pieceSaver.savePiece(any()) } returns updatedPiece

        val result = pieceService.updateProblemOrder(pieceId, request)

        assertThat(result.pieceId).isEqualTo(1L)
        assertThat(result.problemCount).isEqualTo(3)
        assertThat(result.problems[0].problemId).isEqualTo(2L)
        assertThat(result.problems[1].problemId).isEqualTo(3L)
        assertThat(result.problems[2].problemId).isEqualTo(1L)

        verify { pieceFinder.getWithId(pieceId) }
        verify { pieceSaver.savePiece(any()) }
    }

    @Test
    fun `순서 변경 서비스 호출 순서가 올바르다`() {
        val pieceId = Piece.PieceId(1L)
        val request = ProblemOrderUpdateRequest(
            problemId = 1L,
            targetPosition = 1
        )

        val originalProblems = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 10
            ),
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(2L),
                    unitCode = "uc1580",
                    level = 2,
                    problemType = ProblemType.SELECTION,
                    answer = "2"
                ),
                sequence = 20
            )
        )

        val originalPiece = Piece(
            pieceId = pieceId,
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = originalProblems
        )

        val updatedPiece = Piece(
            pieceId = pieceId,
            name = "테스트 학습지",
            teacherId = 1L,
            problemsWithSequence = originalProblems
        )

        every { pieceFinder.getWithId(pieceId) } returns originalPiece
        every { pieceSaver.savePiece(any()) } returns updatedPiece

        pieceService.updateProblemOrder(pieceId, request)

        verify(exactly = 1) { pieceFinder.getWithId(pieceId) }
        verify(exactly = 1) { pieceSaver.savePiece(any()) }
    }

    @Test
    fun `학습지 출제가 성공적으로 처리된다`() {
        val pieceId = Piece.PieceId(1L)
        val request = PieceAssignRequest(
            teacherId = 1L,
            studentIds = listOf(2L, 3L)
        )

        val piece = createPieceWithId(pieceId, teacherId = 1L)
        val studentUserIds = listOf(User.UserId(2L), User.UserId(3L))
        val alreadyAssignedStudentIds = emptyList<User.UserId>()
        val savedStudentPieces = listOf(
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(1L),
                studentId = User.UserId(2L),
                pieceId = pieceId
            ),
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(2L),
                studentId = User.UserId(3L),
                pieceId = pieceId
            )
        )

        every { pieceFinder.getWithId(pieceId) } returns piece
        every { userFinder.validateStudentsExist(studentUserIds) } just runs
        every { studentPieceFinder.findAssignedStudentIds(studentUserIds, pieceId) } returns alreadyAssignedStudentIds
        every { studentPieceSaver.saveAll(any()) } returns savedStudentPieces

        val result = pieceService.assignToStudents(pieceId, request)

        assertThat(result.pieceId).isEqualTo(1L)
        assertThat(result.pieceName).isEqualTo("테스트 학습지")
        assertThat(result.assignedStudentCount).isEqualTo(2)
        assertThat(result.skippedStudentCount).isEqualTo(0)

        verify { pieceFinder.getWithId(pieceId) }
        verify { userFinder.validateStudentsExist(studentUserIds) }
        verify { studentPieceFinder.findAssignedStudentIds(studentUserIds, pieceId) }
        verify { studentPieceSaver.saveAll(any()) }
    }

    @Test
    fun `이미 출제받은 학생이 있을 때 부분 출제가 처리된다`() {
        val pieceId = Piece.PieceId(1L)
        val request = PieceAssignRequest(
            teacherId = 1L,
            studentIds = listOf(2L, 3L, 4L)
        )

        val piece = createPieceWithId(pieceId, teacherId = 1L)
        val studentUserIds = listOf(User.UserId(2L), User.UserId(3L), User.UserId(4L))
        val alreadyAssignedStudentIds = listOf(User.UserId(2L))
        val savedStudentPieces = listOf(
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(1L),
                studentId = User.UserId(3L),
                pieceId = pieceId
            ),
            StudentPiece(
                studentPieceId = StudentPiece.StudentPieceId(2L),
                studentId = User.UserId(4L),
                pieceId = pieceId
            )
        )

        every { pieceFinder.getWithId(pieceId) } returns piece
        every { userFinder.validateStudentsExist(studentUserIds) } just runs
        every { studentPieceFinder.findAssignedStudentIds(studentUserIds, pieceId) } returns alreadyAssignedStudentIds
        every { studentPieceSaver.saveAll(any()) } returns savedStudentPieces

        val result = pieceService.assignToStudents(pieceId, request)

        assertThat(result.pieceId).isEqualTo(1L)
        assertThat(result.assignedStudentCount).isEqualTo(2)
        assertThat(result.skippedStudentCount).isEqualTo(1)

        verify { pieceFinder.getWithId(pieceId) }
        verify { userFinder.validateStudentsExist(studentUserIds) }
        verify { studentPieceFinder.findAssignedStudentIds(studentUserIds, pieceId) }
        verify { studentPieceSaver.saveAll(any()) }
    }

    @Test
    fun `모든 학생이 이미 출제받은 경우 저장하지 않는다`() {
        val pieceId = Piece.PieceId(1L)
        val request = PieceAssignRequest(
            teacherId = 1L,
            studentIds = listOf(2L, 3L)
        )

        val piece = createPieceWithId(pieceId, teacherId = 1L)
        val studentUserIds = listOf(User.UserId(2L), User.UserId(3L))
        val alreadyAssignedStudentIds = listOf(User.UserId(2L), User.UserId(3L))

        every { pieceFinder.getWithId(pieceId) } returns piece
        every { userFinder.validateStudentsExist(studentUserIds) } just runs
        every { studentPieceFinder.findAssignedStudentIds(studentUserIds, pieceId) } returns alreadyAssignedStudentIds

        val result = pieceService.assignToStudents(pieceId, request)

        assertThat(result.assignedStudentCount).isEqualTo(0)
        assertThat(result.skippedStudentCount).isEqualTo(2)

        verify { pieceFinder.getWithId(pieceId) }
        verify { userFinder.validateStudentsExist(studentUserIds) }
        verify { studentPieceFinder.findAssignedStudentIds(studentUserIds, pieceId) }
        verify(exactly = 0) { studentPieceSaver.saveAll(any()) }
    }

    @Test
    fun `학생이 출제받은 학습지의 문제목록을 정상적으로 조회한다`() {
        val pieceId = Piece.PieceId(1L)
        val studentId = User.UserId(2L)
        val piece = createPieceWithId(pieceId, teacherId = 1L)

        every { studentPieceFinder.validateStudentHasPiece(studentId, pieceId) } just runs
        every { pieceFinder.getWithId(pieceId) } returns piece

        val result = pieceService.getProblemsForStudent(pieceId, studentId)

        assertThat(result.pieceId).isEqualTo(1L)
        assertThat(result.pieceName).isEqualTo("테스트 학습지")
        assertThat(result.teacherId).isEqualTo(1L)
        assertThat(result.problemCount).isEqualTo(1)
        assertThat(result.problems).hasSize(1)

        val problemResponse = result.problems[0]
        assertThat(problemResponse.problemId).isEqualTo(1L)
        assertThat(problemResponse.unitCode).isEqualTo("uc1580")
        assertThat(problemResponse.level).isEqualTo(1)
        assertThat(problemResponse.problemType).isEqualTo("SELECTION")
        assertThat(problemResponse.sequence).isEqualTo(10)

        verify { studentPieceFinder.validateStudentHasPiece(studentId, pieceId) }
        verify { pieceFinder.getWithId(pieceId) }
    }

    @Test
    fun `학습지 채점이 성공적으로 처리된다`() {
        val pieceId = Piece.PieceId(1L)
        val studentId = User.UserId(2L)
        val request = com.lms.core_domain.piece.domain.request.PieceScoreRequest(
            studentId = studentId.value,
            answers = listOf(
                com.lms.core_domain.piece.domain.request.StudentAnswerRequest(
                    problemId = 1L,
                    studentAnswer = "정답1"
                )
            )
        )

        val piece = createPieceWithId(pieceId, teacherId = 1L)
        val scoreResults = listOf(
            com.lms.core_domain.piece.domain.response.ScoreResultResponse(
                problemId = 1L,
                studentAnswer = "정답1",
                correctAnswer = "정답1",
                isCorrect = true
            ) to com.lms.core_domain.studentanswer.domain.StudentAnswer(
                studentId = studentId,
                pieceId = pieceId,
                problemId = com.lms.core_domain.problem.domain.Problem.ProblemId(1L),
                studentAnswer = "정답1",
                isCorrect = true
            )
        )

        every { studentPieceFinder.validateStudentHasPiece(studentId, pieceId) } just runs
        every { studentAnswerFinder.hasSubmittedAnswers(studentId, pieceId) } returns false
        every { pieceFinder.getWithId(pieceId) } returns piece
        every { studentAnswerScorer.scoreAnswers(studentId, pieceId, piece, request) } returns scoreResults
        every { studentAnswerSaver.saveAll(any()) } returns scoreResults.map { it.second }

        val result = pieceService.scoreAnswers(pieceId, request)

        assertThat(result.pieceId).isEqualTo(1L)
        assertThat(result.studentId).isEqualTo(2L)
        assertThat(result.totalProblems).isEqualTo(1)
        assertThat(result.correctCount).isEqualTo(1)
        assertThat(result.scoreRate).isEqualTo(100.0)
        assertThat(result.results).hasSize(1)
        assertThat(result.results[0].isCorrect).isTrue()

        verify { studentPieceFinder.validateStudentHasPiece(studentId, pieceId) }
        verify { studentAnswerFinder.hasSubmittedAnswers(studentId, pieceId) }
        verify { pieceFinder.getWithId(pieceId) }
        verify { studentAnswerScorer.scoreAnswers(studentId, pieceId, piece, request) }
        verify { studentAnswerSaver.saveAll(any()) }
    }

    @Test
    fun `이미 답변을 제출한 학습지는 재채점할 수 없다`() {
        val pieceId = Piece.PieceId(1L)
        val studentId = User.UserId(2L)
        val request = com.lms.core_domain.piece.domain.request.PieceScoreRequest(
            studentId = studentId.value,
            answers = listOf(
                com.lms.core_domain.piece.domain.request.StudentAnswerRequest(
                    problemId = 1L,
                    studentAnswer = "정답1"
                )
            )
        )

        every { studentPieceFinder.validateStudentHasPiece(studentId, pieceId) } just runs
        every { studentAnswerFinder.hasSubmittedAnswers(studentId, pieceId) } returns true

        assertThatThrownBy {
            pieceService.scoreAnswers(pieceId, request)
        }.isInstanceOf(BusinessException::class.java)
            .hasMessage("Answers have already been submitted for this piece")

        verify { studentPieceFinder.validateStudentHasPiece(studentId, pieceId) }
        verify { studentAnswerFinder.hasSubmittedAnswers(studentId, pieceId) }
        verify(exactly = 0) { pieceFinder.getWithId(any()) }
        verify(exactly = 0) { studentAnswerScorer.scoreAnswers(any(), any(), any(), any()) }
        verify(exactly = 0) { studentAnswerSaver.saveAll(any()) }
    }

    @Test
    fun `학습지 통계 분석이 성공적으로 처리된다`() {
        val pieceId = Piece.PieceId(1L)
        val teacherId = 1L
        val piece = createPieceWithId(pieceId, teacherId)
        val assignedStudents = listOf(User.UserId(2L), User.UserId(3L))
        val studentAnswers = listOf(
            com.lms.core_domain.studentanswer.domain.StudentAnswer(
                studentId = User.UserId(2L),
                pieceId = pieceId,
                problemId = Problem.ProblemId(1L),
                studentAnswer = "정답",
                isCorrect = true
            ),
            com.lms.core_domain.studentanswer.domain.StudentAnswer(
                studentId = User.UserId(3L),
                pieceId = pieceId,
                problemId = Problem.ProblemId(1L),
                studentAnswer = "오답",
                isCorrect = false
            )
        )

        every { pieceFinder.getWithId(pieceId) } returns piece
        every { studentPieceFinder.findAssignedStudents(pieceId) } returns assignedStudents
        every { studentAnswerFinder.findByPieceId(pieceId) } returns studentAnswers

        val result = pieceService.analyzePiece(pieceId, teacherId)

        assertThat(result.pieceId).isEqualTo(1L)
        assertThat(result.pieceName).isEqualTo("테스트 학습지")
        assertThat(result.teacherId).isEqualTo(1L)
        assertThat(result.totalAssignedStudents).isEqualTo(2)
        assertThat(result.submittedStudents).isEqualTo(2)
        assertThat(result.submissionRate).isEqualTo(100.0)
        assertThat(result.studentStatistics).hasSize(2)
        assertThat(result.problemStatistics).hasSize(1)

        verify { pieceFinder.getWithId(pieceId) }
        verify { studentPieceFinder.findAssignedStudents(pieceId) }
        verify { studentAnswerFinder.findByPieceId(pieceId) }
    }

    @Test
    fun `다른 선생님의 학습지 통계는 조회할 수 없다`() {
        val pieceId = Piece.PieceId(1L)
        val teacherId = 2L // 다른 선생님
        val piece = createPieceWithId(pieceId, teacherId = 1L) // 원래 선생님은 1L

        every { pieceFinder.getWithId(pieceId) } returns piece

        assertThatThrownBy {
            pieceService.analyzePiece(pieceId, teacherId)
        }.isInstanceOf(BusinessException::class.java)
            .hasMessage("학습지에 대한 권한이 없습니다.")

        verify { pieceFinder.getWithId(pieceId) }
        verify(exactly = 0) { studentPieceFinder.findAssignedStudents(any()) }
        verify(exactly = 0) { studentAnswerFinder.findByPieceId(any()) }
    }

    @Test
    fun `출제받지 않은 학생이 학습지 문제를 조회하려고 하면 예외가 발생한다`() {
        val pieceId = Piece.PieceId(1L)
        val studentId = User.UserId(2L)

        every { studentPieceFinder.validateStudentHasPiece(studentId, pieceId) } throws BusinessException("Student is not assigned to this piece")

        assertThatThrownBy {
            pieceService.getProblemsForStudent(pieceId, studentId)
        }.isInstanceOf(BusinessException::class.java)
            .hasMessage("Student is not assigned to this piece")

        verify { studentPieceFinder.validateStudentHasPiece(studentId, pieceId) }
        verify(exactly = 0) { pieceFinder.getWithId(any()) }
    }

    private fun createPieceWithId(pieceId: Piece.PieceId, teacherId: Long = 1L): Piece {
        val problems = listOf(
            ProblemWithSequence(
                problem = Problem(
                    problemId = Problem.ProblemId(1L),
                    unitCode = "uc1580",
                    level = 1,
                    problemType = ProblemType.SELECTION,
                    answer = "1"
                ),
                sequence = 10
            )
        )
        return Piece(
            pieceId = pieceId,
            name = "테스트 학습지",
            teacherId = teacherId,
            problemsWithSequence = problems
        )
    }
}
