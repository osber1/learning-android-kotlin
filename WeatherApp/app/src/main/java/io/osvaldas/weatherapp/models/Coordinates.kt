package io.osvaldas.weatherapp.models

import java.io.Serializable

data class Coordinates(
    val latitude: Double,
    val longitude: Double
) : Serializable