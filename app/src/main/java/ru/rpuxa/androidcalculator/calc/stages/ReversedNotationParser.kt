package ru.rpuxa.androidcalculator.calc.stages

import ru.rpuxa.androidcalculator.calc.BinaryOperator
import ru.rpuxa.androidcalculator.calc.Element
import ru.rpuxa.androidcalculator.calc.Function
import ru.rpuxa.androidcalculator.calc.Num
import ru.rpuxa.androidcalculator.calc.elements.*
import java.util.*
import kotlin.collections.ArrayList

object ReversedNotationParser {

    fun toReversedNotation(elements: List<Element>): List<Element> {
        val result = ArrayList<Element>()
        val stack = Stack<Element>()
        for (current in elements) {
            when {
                current is Num || current is Function && !current.prefix -> {
                    result.add(current)
                }

                current is Function && current.prefix -> {
                    stack.push(current)
                }

                current is Comma -> {
                    while (true) {
                        if (stack.isEmpty()) parseError("Wrong comma!")
                        val element = stack.pop()
                        if (element is OpeningBracket) {
                            stack.push(element)
                            break
                        }
                        result.add(element)
                    }
                }

                current is BinaryOperator -> {
                    while (stack.isNotEmpty()) {
                        val element = stack.peek()
                        if (element is Function && element.prefix ||
                            element is BinaryOperator && (
                                    element.priority > current.priority ||
                                            element.priority == current.priority &&
                                            !element.rightAssociative
                                    )
                        ) {
                            result.add(stack.pop())
                        } else {
                            break
                        }
                    }

                    stack.push(current)
                }
                current is OpeningBracket -> {
                    stack.push(current)
                }

                current is ClosingBracket -> {
                    while (true) {
                        if (stack.isEmpty()) parseError("Wrong brackets!")
                        val element = stack.pop()
                        if (element is OpeningBracket) break
                        result.add(element)
                    }
                }

            }
        }

        while (stack.isNotEmpty()) {
            result.add(stack.pop())
        }

        return result
    }

    private fun parseError(msg: String): Nothing = error(msg)


}