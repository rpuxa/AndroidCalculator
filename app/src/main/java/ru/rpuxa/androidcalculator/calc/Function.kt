package ru.rpuxa.androidcalculator.calc

abstract class Function : Instruction() {
    override val arity get() = 1
    open val prefix get() = true
}