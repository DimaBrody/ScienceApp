package com.brody.arxiv.shared.summary.data.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.brody.arxiv.shared.settings.general.data.SettingsPreferences
import com.brody.arxiv.shared.summary.data.SummaryPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class SummaryPreferencesSerializer @Inject constructor() : Serializer<SummaryPreferences> {
    override val defaultValue: SummaryPreferences = SummaryPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SummaryPreferences =
        try {
            // readFrom is already called on the data store background thread
            SummaryPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: SummaryPreferences, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}