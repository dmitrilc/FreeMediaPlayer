package com.dimitrilc.freemediaplayer.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object BottomNavProtoSerializer: Serializer<com.dimitrilc.freemediaplayer.proto.BottomNavProto> {
    override val defaultValue: com.dimitrilc.freemediaplayer.proto.BottomNavProto
        get() = com.dimitrilc.freemediaplayer.proto.BottomNavProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): com.dimitrilc.freemediaplayer.proto.BottomNavProto {
        try {
            return com.dimitrilc.freemediaplayer.proto.BottomNavProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    }

    override suspend fun writeTo(t: com.dimitrilc.freemediaplayer.proto.BottomNavProto, output: OutputStream) {
        t.writeTo(output)
    }

}

val Context.bottomNavProtoDataStore: DataStore<com.dimitrilc.freemediaplayer.proto.BottomNavProto> by dataStore(
    fileName = "bottom_nav.pb",
    serializer = BottomNavProtoSerializer
)