package com.brody.arxiv.shared.subjects.models.presentation

sealed interface SubjectsRequest {
    data object Waiting : SubjectsRequest
    data object Onboarding : SubjectsRequest
    data class Filters(val excludedIds: Set<Int>?) : SubjectsRequest
}