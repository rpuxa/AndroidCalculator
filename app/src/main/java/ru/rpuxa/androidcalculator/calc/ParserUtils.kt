package ru.rpuxa.androidcalculator.calc

import ru.rpuxa.androidcalculator.calc.elements.Literal

operator fun Int.not() = Literal(toDouble())

operator fun Double.not() = Literal(this)

const val PLUS_PRIORITY = 0
const val TIMES_PRIORITY = 1
const val EQUALS_PRIORITY = 2
const val DOT_PRIORITY = 3