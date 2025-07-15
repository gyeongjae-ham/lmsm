package com.lms.core_domain.studentanswer.service

import com.lms.core_domain.studentanswer.domain.StudentAnswer
import com.lms.core_domain.studentanswer.domain.repository.StudentAnswerRepository
import org.springframework.stereotype.Service

@Service
class StudentAnswerSaver(
    private val studentAnswerRepository: StudentAnswerRepository
) {
    
    fun saveAll(studentAnswers: List<StudentAnswer>): List<StudentAnswer> {
        return studentAnswerRepository.saveAll(studentAnswers)
    }
}