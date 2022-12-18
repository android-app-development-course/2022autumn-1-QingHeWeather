package com.qingheweather.android.logic.model

import java.util.*

class HourlyResponse (val status: String, val result: Result) {

    class Result(val hourly: Hourly)

    class Hourly(val temperature: List<Temperature>, val skycon: List<Skycon>)

    class Temperature(val value: Float)

    class Skycon(val value: String, val datetime: String)
}
