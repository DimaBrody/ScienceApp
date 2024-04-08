package com.brody.arxiv.work.summary.converters

import com.brody.arxiv.work.summary.models.SummaryWorkerState
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json

class WorkerStateConverter(private val json: Json) {

    // Function to serialize a SummaryWorkerState to String
    fun serialize(state: SummaryWorkerState): String {
        return json.encodeToString(PolymorphicSerializer(SummaryWorkerState::class), state)
    }

    // Function to deserialize a String back to SummaryWorkerState
    fun deserialize(string: String): SummaryWorkerState {
        return json.decodeFromString(PolymorphicSerializer(SummaryWorkerState::class), string)
    }
}