package ru.rpuxa.androidcalculator.graph3d

import ru.rpuxa.androidcalculator.CallFromNative
import ru.rpuxa.androidcalculator.sqr
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sqrt

data class Camera(
    @CallFromNative
    var x: Float,

    @CallFromNative
    var y: Float,

    @CallFromNative
    var z: Float,

    @CallFromNative
    var horizontalAngle: Float,

    @CallFromNative
    var verticalAngle: Float,

    @CallFromNative
    var cameraWidth: Float,

    @CallFromNative
    var cameraHeight: Float,

    @CallFromNative
    var screenWidth: Float,

    @CallFromNative
    var screenHeight: Float,

    @CallFromNative
    var focus: Float
) {

    fun setLookAt(x: Float, y: Float, z: Float) {
        horizontalAngle = (atan2(y - this.y, x - this.x) - PI / 2).toFloat()
        verticalAngle = atan((z - this.z) / sqrt(sqr(this.x - x) + sqr(this.y - y)))
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }
}