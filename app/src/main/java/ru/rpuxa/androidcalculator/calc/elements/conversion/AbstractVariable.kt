package ru.rpuxa.androidcalculator.calc.elements.conversion

import ru.rpuxa.androidcalculator.calc.Num

class AbstractVariable(val name: String) : Num {
    override fun toString() = name
}