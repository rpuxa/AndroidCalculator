package ru.rpuxa.androidcalculator.calc

abstract class Instruction : Element {
    abstract val id: Int
    abstract val arity: Int

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Instruction

        if (id != other.id) return false
        if (arity != other.arity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + arity
        return result
    }


}