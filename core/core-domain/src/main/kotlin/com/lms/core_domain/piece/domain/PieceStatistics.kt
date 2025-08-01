package com.lms.core_domain.piece.domain

import com.lms.core_domain.piece.domain.response.PieceOverallStatistic
import com.lms.core_domain.piece.domain.response.ProblemStatistic
import com.lms.core_domain.piece.domain.response.StudentStatistic
import com.lms.core_domain.studentanswer.domain.StudentAnswer
import com.lms.core_domain.user.domain.User

class PieceStatistics(
    private val piece: Piece,
    private val assignedStudents: List<User.UserId>,
    private val studentAnswers: List<StudentAnswer>
) {

    // 학생별 성취도 통계 계산 (제출 여부, 정답률, 점수 포함)
    fun calculateStudentStatistics(): List<StudentStatistic> {
        return assignedStudents.map { studentId ->
            val studentAnswersForPiece = studentAnswers.filter { it.getStudentId() == studentId }

            if (studentAnswersForPiece.isNotEmpty()) {
                val correctCount = studentAnswersForPiece.count { it.isCorrect() }
                val totalProblems = studentAnswersForPiece.size
                val scoreRate = (correctCount.toDouble() / totalProblems) * 100

                StudentStatistic(
                    studentId = studentId.value,
                    totalProblems = totalProblems,
                    correctCount = correctCount,
                    scoreRate = scoreRate,
                    hasSubmitted = true
                )
            } else {
                StudentStatistic(
                    studentId = studentId.value,
                    totalProblems = 0,
                    correctCount = 0,
                    scoreRate = 0.0,
                    hasSubmitted = false
                )
            }
        }
    }

    // 문제별 통계 계산 (제출 수, 정답 수, 정답률 포함)
    fun calculateProblemStatistics(): List<ProblemStatistic> {
        return piece.getProblemsWithSequence().map { problemWithSequence ->
            val problem = problemWithSequence.problem
            val problemAnswers = studentAnswers.filter { it.getProblemId() == problem.id }

            val totalSubmissions = problemAnswers.size
            val correctSubmissions = problemAnswers.count { it.isCorrect() }
            val correctRate = if (totalSubmissions > 0) {
                (correctSubmissions.toDouble() / totalSubmissions) * 100
            } else {
                0.0
            }

            ProblemStatistic(
                problemId = problem.id.value,
                unitCode = problem.unitCode,
                level = problem.level,
                problemType = problem.problemType.name,
                totalSubmissions = totalSubmissions,
                correctSubmissions = correctSubmissions,
                correctRate = correctRate
            )
        }
    }

    // 전체 학습지 통계 계산 (배정 학생 수, 제출 학생 수, 제출률 포함)
    fun calculateOverallStatistics(): PieceOverallStatistic {
        val submittedStudents = assignedStudents.count { studentId ->
            studentAnswers.any { it.getStudentId() == studentId }
        }

        val submissionRate = if (assignedStudents.isNotEmpty()) {
            (submittedStudents.toDouble() / assignedStudents.size) * 100
        } else {
            0.0
        }

        return PieceOverallStatistic(
            totalAssignedStudents = assignedStudents.size,
            submittedStudents = submittedStudents,
            submissionRate = submissionRate
        )
    }
}
