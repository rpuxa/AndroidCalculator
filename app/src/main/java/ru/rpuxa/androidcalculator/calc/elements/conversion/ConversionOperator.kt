package ru.rpuxa.androidcalculator.calc.elements.conversion

import ru.rpuxa.androidcalculator.calc.BinaryOperator

open class ConversionOperator(val symbol: Char, override val priority: Int, override val id: Int) : BinaryOperator() {

    override fun toString() = symbol.toString()
}