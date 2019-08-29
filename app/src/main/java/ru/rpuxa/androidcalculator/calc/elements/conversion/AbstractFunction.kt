package ru.rpuxa.androidcalculator.calc.elements.conversion

import ru.rpuxa.androidcalculator.calc.Function
import kotlin.properties.Delegates
import kotlin.properties.Delegates.notNull

class AbstractFunction(val name: String) : Function() {
    override var id: Int by notNull()

    override val prefix get() = true

    override fun toString() = name
}