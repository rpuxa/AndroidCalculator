package ru.rpuxa.androidcalculator.calc.elements.instructions

import ru.rpuxa.androidcalculator.calc.BinaryOperator
import ru.rpuxa.androidcalculator.calc.Instruction
import ru.rpuxa.androidcalculator.calc.InstructionFactory
import ru.rpuxa.androidcalculator.calc.TIMES_PRIORITY

object Divide : BinaryOperator(), InstructionFactory {
    override val id get() = 3

    override val rightAssociative get() = true
    override val priority get() = TIMES_PRIORITY

    override fun toString() = "/"

    override fun instantiate(arity: Int) = this
}