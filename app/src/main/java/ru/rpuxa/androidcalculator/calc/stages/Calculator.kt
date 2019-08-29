package ru.rpuxa.androidcalculator.calc.stages

import ru.rpuxa.androidcalculator.calc.Element
import ru.rpuxa.androidcalculator.calc.Instruction
import ru.rpuxa.androidcalculator.calc.elements.instructions.*
import ru.rpuxa.androidcalculator.calc.not
import java.util.*

object Calculator {


    fun convertPlusToSum(elements: List<Element>): List<Element> = convert(elements, true)

    fun convertTimesToProduct(elements: List<Element>): List<Element> = convert(elements, false)

    private fun convert(elements: List<Element>, isSum: Boolean): List<Element> {
        val minusId = if (isSum) Minus.id else Divide.id
        val plusId = if (isSum) Plus.id else Times.id
        val sumId = if (isSum) Sum.id else Product.id
        val stack = Stack<Element>()
        for (element in elements) {
            if (element !is Instruction || element.id != minusId && element.id != plusId) {
                stack.push(element)
                continue
            }

            if (element.id == minusId) {
                if (isSum) {
                    stack.push(UnaryMinus)
                } else {
                    stack.push(!-1)
                    stack.push(Power)
                }
            }
            val first = stack.pop()
            val tmpStack = Stack<Element>()

            var i = if (first is Instruction) first.arity else 0
            while (i > 0) {
                val e = stack.pop()
                if (e is Instruction) {
                    i += e.arity
                }
                tmpStack.push(e)
                i--
            }

            val second = stack.pop()

            var arity =
                if (second is Instruction && second.id == sumId) {
                    second.arity
                } else {
                    stack.push(second)
                    1
                }

            while (tmpStack.isNotEmpty())
                stack.push(tmpStack.pop())

            arity += if (first is Instruction && first.id == sumId) {
                first.arity
            } else {
                stack.push(first)
                1
            }

            stack.push(if (isSum) Sum(arity) else Product(arity))
        }

        return stack
    }
}