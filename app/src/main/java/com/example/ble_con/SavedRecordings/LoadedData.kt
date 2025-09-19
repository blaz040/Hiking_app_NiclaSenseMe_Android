package com.example.ble_con.SavedRecordings

import co.yml.charts.common.model.Point
import com.example.ble_con.dataManager.repo.DataList
import com.example.ble_con.dataManager.repo.SensorData
import com.example.ble_con.repository.ViewModelData
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder



// TODO CLEAN THIS FILE
@Serializable(with = PointListSerializable::class)
data class LoadedData(
    val temperature:List<Point>,
)
class PointListSerializable():KSerializer<List<Point>>{
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Points", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): List<Point> {
        val string = decoder.decodeString()
        val floats = string.split(":").map { it.toFloat()}
        val list = floats.chunked(2).map { (x,y) -> Point(x,y) }
        return list
    }
    override fun serialize(encoder: Encoder, value: List<Point>) {
        val flat = value.flatMap { listOf(it.x,it.y) }
        val string = flat.joinToString(":")
        encoder.encodeString(string)
    }
}
class PointSerializable():KSerializer<Point>{
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
data class DataMap(
    val name:String,
    val list:List<
            @Serializable(with = PointSerializable::class)Point
            >
)
//TODO add location here
@Serializable
val dataList = listOf(
    DataMap("Temperature", SensorData.temperature.getList()),
    DataMap("Humidity", SensorData.humidity.getList()),
    DataMap("Pressure", SensorData.pressure.getList()),
    DataMap("Air Quality", SensorData.iaq.getList()),
    DataMap("VOC", SensorData.voc.getList()),
    DataMap("CO2", SensorData.co2.getList()),
    DataMap("Altitude", SensorData.altitude.getList()),
    DataMap("Steps", SensorData.steps.getList()),
)

