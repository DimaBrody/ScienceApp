package com.brody.arxiv.shared.settings.ai.data.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.brody.arxiv.shared.settings.ai.data.AiPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class AiPreferencesSerializer @Inject constructor() : Serializer<AiPreferences> {
    override val defaultValue: AiPreferences = AiPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AiPreferences =
        try {
            // readFrom is already called on the data store background thread
            AiPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: AiPreferences, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}