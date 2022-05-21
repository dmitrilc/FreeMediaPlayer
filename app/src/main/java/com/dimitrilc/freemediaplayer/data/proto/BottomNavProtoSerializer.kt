package com.dimitrilc.freemediaplayer.data.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object BottomNavProtoSerializer: Serializer<BottomNavProto> {
    override val defaultValue: BottomNavProto
        get() = BottomNavProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): BottomNavProto {
        try {
            return BottomNavProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: BottomNavProto, output: OutputStream) {
        t.writeTo(output)
    }

}

val Context.bottomNavProtoDataStore: DataStore<BottomNavProto> by dataStore(
    fileName = "bottom_nav.pb",
    serializer = BottomNavProtoSerializer
)