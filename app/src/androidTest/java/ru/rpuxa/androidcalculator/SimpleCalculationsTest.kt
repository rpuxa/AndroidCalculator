package ru.rpuxa.androidcalculator

import androidx.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import ru.rpuxa.androidcalculator.calc.*
import ru.rpuxa.androidcalculator.calc.elements.*
import ru.rpuxa.androidcalculator.calc.elements.instructions.Divide
import ru.rpuxa.androidcalculator.calc.elements.instructions.Minus
import ru.rpuxa.androidcalculator.calc.elements.instructions.Plus
import ru.rpuxa.androidcalculator.calc.elements.instructions.Times
import ru.rpuxa.core.Compiler
import ru.rpuxa.androidcalculator.calc.stages.NativeCalculator
import ru.rpuxa.androidcalculator.calc.stages.ReversedNotationParser

@RunWith(AndroidJUnit4::class)
class SimpleCalculationsTest {

    private var i = 0

    private fun check(expected: Double, vararg elements: Element) {
        println("${i++}) ${elements.joinToString(" ")}")
        val bytes = ru.rpuxa.core.Compiler.compile(ReversedNotationParser.toReversedNotation(elements.toList()))
        val size = NativeCalculator.calculate(bytes, ByteArray(0))
        Assert.assertEquals(1, size)
        val (element) = ru.rpuxa.core.Compiler.decompile(bytes, size)

        if (element !is Literal)
            Assert.fail()
        else
            Assert.assertEquals(expected, element.value, .001)
    }

    @Test
    fun test1() {
        check(3.0, !1, Plus, !2)
    }

    @Test
    fun test2() {
        check(7.0, !1, Plus, !2, Times, !3)
    }

    @Test
    fun test3() {
        check(
            0.0, !1,
            Plus, OpeningBracket, !2, Minus, !3, ClosingBracket
        )
    }

    @Test
    fun test4() {
        check(0.0, !1, Plus, !2, Minus, !3)
    }

    @Test
    fun test5() {
        check(
            1.0 - 2.0 / (3.0 + 4.0), !1, Minus, !2,
            Divide, OpeningBracket, !3, Plus, !4, ClosingBracket
        )
    }

}