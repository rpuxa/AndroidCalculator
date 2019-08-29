package ru.rpuxa.androidcalculator.calc

import ru.rpuxa.androidcalculator.calc.elements.ClosingBracket
import ru.rpuxa.androidcalculator.calc.elements.Comma
import ru.rpuxa.androidcalculator.calc.elements.OpeningBracket
import ru.rpuxa.androidcalculator.calc.elements.instructions.*

object Elements {
    @JvmField
    val INSTRUCTION_FACTORIES = arrayOf(
        Divide,
        Minus,
        Plus,
        Product,
        Sum,
        Times,
        UnaryMinus,
        Power
    )

    @JvmField
    val PARSE_ELEMENTS = arrayOf(
        ClosingBracket,
        Comma,
        OpeningBracket
    ) + INSTRUCTION_FACTORIES.filterIsInstance<BinaryOperator>()

    init {
       /* if (BuildConfig.DEBUG) {
            for (id in INSTRUCTION_FACTORIES.indices) {
                if (INSTRUCTION_FACTORIES.count { it.id == id } != 1)
                    error("Wrong id")
            }
        }*/
    }


    fun getInstruction(arity: Int, id: Int): Instruction =
        INSTRUCTION_FACTORIES.first { it.id == id }.instantiate(arity)
}