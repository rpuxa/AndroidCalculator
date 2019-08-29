package ru.rpuxa.androidcalculator.calc.elements.instructions

import ru.rpuxa.androidcalculator.calc.Function
import ru.rpuxa.androidcalculator.calc.InstructionFactory

object UnaryMinus : Function(), InstructionFactory {
    override val id get() = 6

    override fun toString() = "~"

    override fun instantiate(arity: Int) = this
}