package ru.rpuxa.androidcalculator.calc.elements

import ru.rpuxa.androidcalculator.calc.Element
import ru.rpuxa.androidcalculator.calc.Num

class Literal(val value: Double) : Num {

    override fun toString() = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Literal

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}