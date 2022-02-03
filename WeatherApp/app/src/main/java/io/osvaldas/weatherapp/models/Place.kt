package io.osvaldas.weatherapp.models

import java.io.Serializable

data class Place(
    val code: String,
    val name: String,
    val administrativeDivision: String,
    val country: String,
    val countryCode: String,
    val coordinates: Coordinates
) : Serializable