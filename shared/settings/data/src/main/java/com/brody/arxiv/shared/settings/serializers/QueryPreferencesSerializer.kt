package com.brody.arxiv.shared.settings.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.brody.arxiv.shared.settings.data.QueryPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class QueryPreferencesSerializer @Inject constructor() : Serializer<QueryPreferences> {
    override val defaultValue: QueryPreferences = QueryPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): QueryPreferences =
        try {
            // readFrom is already called on the data store background thread
            QueryPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: QueryPreferences, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}