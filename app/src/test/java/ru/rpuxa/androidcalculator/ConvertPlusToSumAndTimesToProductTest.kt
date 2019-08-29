package ru.rpuxa.androidcalculator

import androidx.test.runner.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import ru.rpuxa.androidcalculator.calc.*
import ru.rpuxa.androidcalculator.calc.elements.instructions.*
import ru.rpuxa.androidcalculator.calc.stages.Calculator

class ConvertPlusToSumAndTimesToProductTest {

    private fun checkSum(expected: Array<Element>, elements: Array<Element>) {
        val actual = Calculator.convertPlusToSum(elements.toList())
        Assert.assertEquals(expected.joinToString(", "), actual.joinToString(", "))
    }

    private fun checkProduct(expected: Array<Element>, elements: Array<Element>) {
        val actual = Calculator.convertTimesToProduct(elements.toList())
        Assert.assertEquals(expected.joinToString(", "), actual.joinToString(", "))
    }


    @Test
    fun test1() {
        checkSum(
            arrayOf(!1, !2, Sum(2)),
            arrayOf(!1, !2, Plus)
        )
    }

    @Test
    fun test2() {
        checkSum(
            arrayOf(!1, !2, UnaryMinus, Sum(2)),
            arrayOf(!1, !2, Minus)
        )
    }

    @Test
    fun test3() {
        checkSum(
            arrayOf(!1, !2, !3, Sum(3)),
            arrayOf(!1, !2, Plus, !3, Plus)
        )
    }


    @Test
    fun test4() {
        checkSum(
            arrayOf(!1, !2, !3, !4, !5, !6, Sum(6)),
            arrayOf(!1, !2, Plus, !3, Plus, !4, !5, !6, Plus, Plus, Plus)
        )
    }

    @Test
    fun test5() {
        checkSum(
            arrayOf(!1, !2, Times, !3, !4, Times, !5, !6, Divide, UnaryMinus, Sum(3)),
            arrayOf(!1, !2, Times, !3, !4, Times, Plus, !5, !6, Divide, Minus)
        )
    }

    @Test
    fun test6() {
        checkSum(
            arrayOf(!1, !2, Times, !3, !4, Times, !5, !6, Divide, UnaryMinus, !7, UnaryMinus, Sum(4)),
            arrayOf(!1, !2, Times, !3, !4, Times, Plus, !5, !6, Divide, Minus, !7, Minus)
        )
    }

    @Test
    fun test7() {
        checkProduct(
            arrayOf(!1, !2, Product(2)),
            arrayOf(!1, !2, Times)
        )
    }

    @Test
    fun test8() {
        checkProduct(
            arrayOf(!1, !2, !-1, Power, Product(2)),
            arrayOf(!1, !2, Divide)
        )
    }

    @Test
    fun test9() {
        checkProduct(
            arrayOf(!1, !2, !3, !4, Product(3)),
            arrayOf(!1, !2, !3, !4, Times, Times, Times)
        )
    }
}