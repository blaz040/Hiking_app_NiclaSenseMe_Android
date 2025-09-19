package com.example.ble_con.SavedRecordings

import co.yml.charts.common.model.Point

class AnalyzeData(
    val temperatureList: List<Point>,
    val humidityList: List<Point>,
    val pressureList: List<Point>,
    val iaqList: List<Point>,
    val co2List: List<Point>,
    val vocList: List<Point>,
    val altitudeList: List<Point>
) {
    // TODO analyze data (show graphs and stuff)
}