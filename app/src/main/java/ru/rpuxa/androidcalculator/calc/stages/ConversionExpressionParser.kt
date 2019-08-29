package ru.rpuxa.androidcalculator.calc.stages

import ru.rpuxa.androidcalculator.calc.Element
import ru.rpuxa.androidcalculator.calc.Elements
import ru.rpuxa.androidcalculator.calc.elements.OpeningBracket
import ru.rpuxa.androidcalculator.calc.elements.conversion.AbstractFunction
import ru.rpuxa.androidcalculator.calc.elements.conversion.AbstractVariable
import ru.rpuxa.androidcalculator.calc.elements.conversion.Dot
import ru.rpuxa.androidcalculator.calc.elements.conversion.LogicAnd
import ru.rpuxa.androidcalculator.calc.not

object ConversionExpressionParser {

    private val operators = arrayOf(
        Dot, LogicAnd
    )

    private const val CONDITIONS_DIVIDER = "where"
    private const val CONVERSION_DIVIDER = "->"
    private const val ABSTRACT_FUNCTION_PREFIX = '%'
    private const val CONSTANT_PREFIX = '$'

    private fun CharSequence.equalsChars(other: CharSequence): Boolean {
        if (length != other.length) return false
        for (i in indices)
            if (this[i] != other[i])
                return false
        return true
    }

    private fun StringBuilder.deleteLast() = deleteCharAt(lastIndex)
    private fun StringBuilder.deleteFirst() = deleteCharAt(0)

    fun parse(code: String) {
        val tmp = code.split(
            CONVERSION_DIVIDER,
            CONDITIONS_DIVIDER
        )
        val fromExpression = ReversedNotationParser.toReversedNotation(
            parseExpression(tmp[0])
        )
        val condition = if (tmp.size == 3) tmp[1] else null
        val toExpression = ReversedNotationParser.toReversedNotation(
            parseExpression(if (condition == null) tmp[1] else tmp[2])
        )


    }


    fun parseExpression(expression: String): List<Element> {
        var separated: List<Any> = ArrayList<Any>().apply {
            add(expression)
        }

        Elements.PARSE_ELEMENTS.forEach { element ->
            val string = element.toString()
            separated = separated.flatMap { e ->
                if (e is String) {
                    val list = e.split(string)
                    val newList = ArrayList<Any>()
                    repeat(list.size * 2 - 1) { i ->
                        if (i % 2 == 0) {
                            newList.add(list[i / 2])
                        } else {
                            newList.add(element)
                        }
                    }
                    newList
                } else {
                    listOf(e)
                }
            }
        }

        var i = 0

        return separated.map<Any, Element> {
            val nextIndex = ++i
            if (it is Element)
                return@map it

            it as String

            val toDouble = it.toDoubleOrNull()
            if (toDouble != null)
                return@map !toDouble


            if (it.startsWith(ABSTRACT_FUNCTION_PREFIX)) {
                return@map AbstractFunction(it.substring(1))
            }

            if (it.startsWith(CONSTANT_PREFIX)) {
                return@map TODO("Constants")
            }

            val next = separated.getOrNull(nextIndex)

            if (next is OpeningBracket) {
                Elements.PARSE_ELEMENTS.first { e -> e.toString() == it }
            } else {
                AbstractVariable(it)
            }
        }
    }


}
