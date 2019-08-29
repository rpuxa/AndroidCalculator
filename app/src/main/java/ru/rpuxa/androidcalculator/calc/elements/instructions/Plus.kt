package ru.rpuxa.androidcalculator.calc.elements.instructions

import ru.rpuxa.androidcalculator.calc.BinaryOperator
import ru.rpuxa.androidcalculator.calc.InstructionFactory
import ru.rpuxa.androidcalculator.calc.PLUS_PRIORITY

object Plus : BinaryOperator(), InstructionFactory {
    override val id get() = 0

    override val rightAssociative get() = true
    override val priority get() = PLUS_PRIORITY

    override fun toString() = "+"

    override fun instantiate(arity: Int) = this
}