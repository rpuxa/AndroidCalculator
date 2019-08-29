package ru.rpuxa.core

import ru.rpuxa.androidcalculator.calc.Element
import ru.rpuxa.androidcalculator.calc.Elements
import ru.rpuxa.androidcalculator.calc.Instruction
import ru.rpuxa.androidcalculator.calc.elements.Constant
import ru.rpuxa.androidcalculator.calc.elements.Literal
import ru.rpuxa.androidcalculator.calc.elements.conversion.AbstractFunction
import ru.rpuxa.androidcalculator.calc.elements.conversion.AbstractVariable
import java.io.*

object Compiler {

    private const val HEADER_EMPTY = 0
    private const val HEADER_NUMBER = 1
    private const val HEADER_CONSTANT = 2
    private const val HEADER_INSTRUCTION = 3
    private const val HEADER_VARIABLE = 4
    private const val HEADER_ABSTRACT_INSTRUCTION = 5
    private const val HEADER_ABSTRACT_VARIABLE = 6
    private const val HEADER_CONDITION_DIVIDER = 7
    private const val HEADER_CONVERSION_DIVIDER = 8

    /**
     *  header - 1byte
     *  value - 8byte
     *
     *
     */

    fun compile(stream: OutputStream, elements: List<Element>) {
        val abstractFunctions = HashMap<String, Long>()
        val abstractVariables = HashMap<String, Long>()

        elements.forEach { element ->
            if (element is AbstractFunction) {
                abstractFunctions[element.name] = abstractFunctions.size.toLong()
            } else if (element is AbstractVariable) {
                abstractVariables[element.name] = abstractVariables.size.toLong()
            }
        }

        DataOutputStream(stream).apply {
            elements.forEach { element ->
                when (element) {
                    is Literal -> {
                        writeByte(HEADER_NUMBER)
                        writeReversedDouble(element.value)
                    }

                    is Constant -> TODO()

                    is Instruction -> {
                        writeByte(HEADER_INSTRUCTION)
                        writeReversedInt(element.arity)
                        writeReversedInt(element.id)
                    }

                    is AbstractFunction -> {
                        writeByte(HEADER_ABSTRACT_INSTRUCTION)
                        writeReversedLong(abstractFunctions[element.name]!!)
                    }

                    is AbstractVariable -> {
                        writeByte(HEADER_ABSTRACT_VARIABLE)
                        writeReversedLong(abstractVariables[element.name]!!)
                    }
                }
            }
        }
    }

    fun compile(elements: List<Element>): ByteArray {
        val stream = ByteArrayOutputStream()
        compile(stream, elements)
        return stream.toByteArray()
    }

    fun compileCondition(stream: OutputStream, elements: List<Element>) {
        DataOutputStream(stream).apply {
            writeByte(HEADER_CONDITION_DIVIDER)
            writeLong(0)
        }

        compile(stream, elements)
    }

    fun decompile(stream: InputStream): List<Element> {
        val result = ArrayList<Element>()
        DataInputStream(stream).apply {
            while (true) {
                val header = read()
                if (header == -1) break

                when (header) {
                    HEADER_NUMBER -> {
                        result.add(Literal(readReversedDouble()))
                    }

                    HEADER_INSTRUCTION -> {
                        val element = Elements.getInstruction(
                            readReversedInt(),
                            readReversedInt()
                        )
                        result.add(element)
                    }

                    //TODO other

                    else -> error("Unknown header: $header")
                }
            }
        }

        return result
    }

    fun decompile(array: ByteArray, length: Int): List<Element> {
        return decompile(
            ByteArrayInputStream(
                if (length != array.size) array.copyOf(length) else array
            )
        )
    }

}