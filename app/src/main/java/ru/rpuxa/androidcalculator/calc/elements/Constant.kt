package ru.rpuxa.androidcalculator.calc.elements

import ru.rpuxa.androidcalculator.calc.Num

class Constant(val value: Double) : Num {
    override fun toString() = value.toString()
}