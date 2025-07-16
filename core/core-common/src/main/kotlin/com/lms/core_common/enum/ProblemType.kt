package com.lms.core_common.enum

enum class ProblemType {
    ALL,
    SUBJECTIVE,
    SELECTION;

    fun isSame(other: ProblemType): Boolean {
        return this == other
    }
}
