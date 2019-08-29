package ru.rpuxa.androidcalculator.calc

interface InstructionFactory {
    val id: Int

    fun instantiate(arity: Int): Instruction
}