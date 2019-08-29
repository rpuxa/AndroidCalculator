package ru.rpuxa.androidcalculator

import java.io.DataInputStream
import java.io.DataOutputStream

fun sqr(x: Float) = x * x
fun sqr(x: Int) = x * x

fun DataOutputStream.writeReversedDouble(d: Double) {

    writeReversedLong(d.toBits())
}

fun DataOutputStream.writeReversedInt(i: Int) {
    val b1 = (i ushr 0).toByte()
    val b2 = (i ushr 8).toByte()
    val b3 = (i ushr 16).toByte()
    val b4 = (i ushr 24).toByte()
    write(byteArrayOf(b1, b2, b3, b4))
}

fun DataOutputStream.writeReversedLong(l: Long) {
    val first = l.toInt()
    val second = (l ushr 32).toInt()
    writeReversedInt(first)
    writeReversedInt(second)
}

fun DataInputStream.readReversedDouble(): Double {
    return Double.fromBits(readReversedLong())
}

fun DataInputStream.readReversedInt(): Int {
    val b1 = read()
    val b2 = read()
    val b3 = read()
    val b4 = read()
    return (b4 shl 24) or (b3 shl 16) or (b2 shl 8) or b1
}

fun DataInputStream.readReversedLong(): Long {
    val first = readReversedInt().toLong() and ((1L shl 32) - 1L)
    val second = readReversedInt().toLong()

    return (second shl 32) or first
}
