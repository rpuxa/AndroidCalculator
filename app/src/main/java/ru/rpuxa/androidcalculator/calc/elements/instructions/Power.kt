package ru.rpuxa.androidcalculator.calc.elements.instructions

import ru.rpuxa.androidcalculator.calc.Instruction
import ru.rpuxa.androidcalculator.calc.InstructionFactory

object Power : Instruction(), InstructionFactory {

    override val id get() = 7

    override val arity: Int get() = 2

    override fun toString() = "^"

    override fun instantiate(arity: Int) = this
}