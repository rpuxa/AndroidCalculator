package ru.rpuxa.androidcalculator.graph3d

import ru.rpuxa.androidcalculator.CallFromNative

data class Graph3dParams(
    @CallFromNative
    var polygonsCount: Int,

    @CallFromNative
    var polygonSize: Float
) {
    @CallFromNative
    val polygonStart
        get() = (polygonSize * polygonsCount) / -2
}