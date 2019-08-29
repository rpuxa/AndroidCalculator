package ru.rpuxa.androidcalculator.calc.elements.instructions

import ru.rpuxa.androidcalculator.calc.Function
import ru.rpuxa.androidcalculator.calc.InstructionFactory

class Sum(override val arity: Int) : Function() {
    override val id get() = ID

    override fun toString() = "sum"

    companion object : InstructionFactory {
        private const val ID = 4

        override val id get() = ID

        override fun instantiate(arity: Int) = Sum(arity)
    }
}