package com.lms.core_domain.studentanswer.service

import com.lms.core_common.exception.BusinessException
import com.lms.core_domain.piece.domain.Piece
import com.lms.core_domain.piece.domain.request.PieceScoreRequest
import com.lms.core_domain.piece.domain.response.ScoreResultResponse
import com.lms.core_domain.studentanswer.domain.StudentAnswer
import com.lms.core_domain.user.domain.User
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

@Service
class StudentAnswerScorer {
    private val executor = Executors.newVirtualThreadPerTaskExecutor()

    // 가상 스레드를 활용한 병렬 처리로 여러 문제를 동시에 채점하는 로직
    fun scoreAnswers(
        studentId: User.UserId,
        pieceId: Piece.PieceId,
        piece: Piece,
        request: PieceScoreRequest
    ): List<Pair<ScoreResultResponse, StudentAnswer>> {
        val futures = request.answers.map { answerRequest ->
            CompletableFuture.supplyAsync({
                val problem = piece.getProblemsWithSequence()
                    .find { it.problem.id.value == answerRequest.problemId }
                    ?.problem
                    ?: throw BusinessException("Problem not found in piece: ${answerRequest.problemId}")

                val studentAnswer = StudentAnswer.score(
                    studentId = studentId,
                    pieceId = pieceId,
                    problemId = problem.id,
                    studentAnswer = answerRequest.studentAnswer,
                    correctAnswer = problem.answer
                )

                val scoreResult = ScoreResultResponse(
                    problemId = problem.id.value,
                    studentAnswer = answerRequest.studentAnswer,
                    correctAnswer = problem.answer,
                    isCorrect = studentAnswer.isCorrect()
                )

                scoreResult to studentAnswer
            }, executor)
        }

        return futures.map { it.get() }
    }
}
