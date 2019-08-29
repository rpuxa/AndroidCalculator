package ru.rpuxa.androidcalculator.calc.elements.instructions

import ru.rpuxa.androidcalculator.calc.Function
import ru.rpuxa.androidcalculator.calc.Instruction
import ru.rpuxa.androidcalculator.calc.InstructionFactory

class Product(override val arity: Int) : Function() {

    override val id get() = ID

    override fun toString() = "product"

    companion object : InstructionFactory {
        private const val ID = 5


        override val id get() = ID


        override fun instantiate(arity: Int) = Product(arity)
    }
}
