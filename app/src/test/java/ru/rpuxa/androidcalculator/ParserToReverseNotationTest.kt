package ru.rpuxa.androidcalculator

import org.junit.Assert.*
import org.junit.Test
import ru.rpuxa.androidcalculator.calc.Element
import ru.rpuxa.androidcalculator.calc.stages.ReversedNotationParser
import ru.rpuxa.androidcalculator.calc.elements.*
import ru.rpuxa.androidcalculator.calc.elements.instructions.Divide
import ru.rpuxa.androidcalculator.calc.elements.instructions.Minus
import ru.rpuxa.androidcalculator.calc.elements.instructions.Plus
import ru.rpuxa.androidcalculator.calc.elements.instructions.Times
import ru.rpuxa.androidcalculator.calc.not

class ParserToReverseNotationTest {

    private var i = 1

    private fun check(expected: String, vararg elements: Element) {
        println("${i++}) ${elements.joinToString(" ")}")
        val actual = ReversedNotationParser.toReversedNotation(elements.toList()).joinToString(" ")
        assertEquals(expected, actual)
    }

    @Test
    fun test1() {
        check("1.0 2.0 +", !1, Plus, !2)
    }

    @Test
    fun test2() {
        check("1.0 2.0 3.0 - +", !1,
            Plus, OpeningBracket, !2, Minus, !3, ClosingBracket)
    }

    @Test
    fun test3() {
        check("2.0 2.0 2.0 * +", !2, Plus, !2, Times, !2)
    }

    @Test
    fun test4() {
        check("1.0 2.0 3.0 4.0 + / -", !1, Minus, !2,
            Divide, OpeningBracket, !3, Plus, !4, ClosingBracket)
    }


}