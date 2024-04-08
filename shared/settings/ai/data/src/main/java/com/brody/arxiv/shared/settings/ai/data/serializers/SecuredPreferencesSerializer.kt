package com.brody.arxiv.shared.settings.ai.data.serializers

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.brody.arxiv.shared.settings.ai.models.data.ModelKeys
import com.google.crypto.tink.Aead
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class SecuredPreferencesSerializer @Inject constructor(
    private val aead: Aead
) : Serializer<ModelKeys> {
    override val defaultValue: ModelKeys = ModelKeys.createDefault()

    override suspend fun readFrom(input: InputStream): ModelKeys {
        return try {
            val encryptedInput = input.readBytes()

            val decryptedInput = if (encryptedInput.isNotEmpty()) {
                aead.decrypt(encryptedInput, null)
            } else {
                encryptedInput
            }

            ProtoBuf.decodeFromByteArray(ModelKeys.serializer(), decryptedInput)
        } catch (e: SerializationException) {
            throw CorruptionException("Error deserializing proto", e)
        }
    }

    override suspend fun writeTo(t: ModelKeys, output: OutputStream) {
        val byteArray = ProtoBuf.encodeToByteArray(ModelKeys.serializer(), t)
        val encryptedBytes = aead.encrypt(byteArray, null)

        output.write(encryptedBytes)
    }
}