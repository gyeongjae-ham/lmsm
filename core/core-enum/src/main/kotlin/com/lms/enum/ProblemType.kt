package com.lms.enum

enum class ProblemType {
    ALL,
    SUBJECTIVE,
    SELECTION;

    fun isSame(other: ProblemType): Boolean {
        return this == other
    }
}
