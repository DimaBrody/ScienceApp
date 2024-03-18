package com.brody.arxiv.shared.settings.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.brody.arxiv.shared.settings.data.SettingsPreferences
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class DefaultPreferencesSerializer @Inject constructor() : Serializer<SettingsPreferences> {
    override val defaultValue: SettingsPreferences = SettingsPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SettingsPreferences =
        try {
            // readFrom is already called on the data store background thread
            SettingsPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: SettingsPreferences, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}