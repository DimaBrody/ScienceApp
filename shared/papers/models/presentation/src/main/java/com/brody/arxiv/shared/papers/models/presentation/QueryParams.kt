package com.brody.arxiv.shared.papers.models.presentation

import com.brody.arxiv.shared.papers.models.domain.StringParam

enum class SortBy(
    private val value: String,
    override val displayName: String
) : StringParam, DisplayableEnum {
    RELEVANCE("relevance", "Relevance"),
    LAST_UPDATED_DATE("lastUpdatedDate", "Last updated time"),
    SUBMITTED_DATE("submittedDate", "Upload time");

    override fun convertToString(): String {
        return this.value
    }
}

enum class SortOrder(
    private val value: String,
    override val displayName: String
) : StringParam, DisplayableEnum {
    DESC("descending", "Descending"),
    ASC("ascending", "Ascending");

    override fun convertToString(): String {
        return this.value
    }
}

interface DisplayableEnum {
    val displayName: String
}

sealed class PrefixParam(
    open val value: String,
    private val prefix: String,
) : StringParam {
    class Title(
        override val value: String
    ) : PrefixParam(value, "ti")

    class Author(
        override val value: String
    ) : PrefixParam(value, "au")

    class Cat(
        override val value: String
    ) : PrefixParam(value, "cat")

    class All(
        override val value: String
    ) : PrefixParam(value, "all")

    override fun convertToString(): String {
        return "${prefix}:${value}"
    }
}

