package ru.rpuxa.androidcalculator.calc

abstract class BinaryOperator : Instruction() {
    override val arity get() = 2
    open val rightAssociative: Boolean get() = true
    abstract val priority: Int
}