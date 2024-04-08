package com.brody.arxiv.features.summary.presentation.models

enum class SettingsSection(
    val text: String,
    val description: String? = null
) {
    TEMPERATURE(
        "Temperature",
        "Adjusts creativity level. A higher value generates more varied responses, while a lower value produces more predictable ones."
    ),
    STREAM(
        "Stream",
        "Enables continuous text output. Disabled stream will return whole summary at once."
    ),
    LANGUAGE("Language")
}