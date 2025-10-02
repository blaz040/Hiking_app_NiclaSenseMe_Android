package com.example.ble_con.fileManager

import co.yml.charts.common.model.Point
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
object PointSerializer:KSerializer<Point>{
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Point", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Point{
        val string = decoder.decodeString()
        val floats = string.split(":").map { it.toFloat()}
        return Point(floats[0],floats[1])
    }

    override fun serialize(encoder: Encoder, value: Point) {
        val string = "${value.x}:${value.y}"
        encoder.encodeString(string)
    }
}

@Serializable
object LatLngSerializer:KSerializer<LatLng>{
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LatLng", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LatLng{
        val string = decoder.decodeString()
        val floats = string.split(":").map { it.toDouble()}
        return LatLng(floats[0],floats[1])
    }

    override fun serialize(encoder: Encoder, value: LatLng) {
        val string = "${value.latitude}:${value.longitude}"
        encoder.encodeString(string)
    }
}

