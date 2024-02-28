package com.brody.arxiv.shared.subjects.models.domain

data class UserSubjects(
    val items: List<Int>?
) {
    companion object {
        private fun empty() = UserSubjects(emptyList())
        fun from(items: List<Int>?) = items?.let { UserSubjects(it) } ?: empty()
    }
}
