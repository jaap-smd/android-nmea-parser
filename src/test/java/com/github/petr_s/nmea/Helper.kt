package com.github.petr_s.nmea

import org.hamcrest.Description
import kotlin.jvm.JvmOverloads
import org.mockito.ArgumentMatcher
import kotlin.math.abs

object Helper {
    @JvmStatic
    fun roughlyEq(expected: Double, delta: Double = 0.0001): ArgumentMatcher<Double> {
        return object : ArgumentMatcher<Double> {
            override fun matches(argument: Double): Boolean {
                return abs(expected - argument) <= delta
            }

            fun describeTo(description: Description) {
                description.appendText(
                    "$expected±$delta"
                )
            }
        }
    }

    @JvmStatic
    fun roughlyEq(expected: Float, delta: Float = 0.0001f): ArgumentMatcher<Float> {
        return object : ArgumentMatcher<Float> {
            override fun matches(argument: Float): Boolean {
                return abs(expected - argument) <= delta
            }

            fun describeTo(description: Description) {
                description.appendText(
                    "$expected±$delta"
                )
            }
        }
    }

    @JvmStatic
    fun <T> eq(expected: Set<T>): ArgumentMatcher<Set<*>> {
        return object : ArgumentMatcher<Set<*>> {
            override fun matches(argument: Set<*>): Boolean {
                return argument == expected
            }

            fun describeTo(description: Description) {
                description.appendText(expected.toString())
            }
        }
    }

    @JvmStatic
    fun <T> eq(expected: List<T>): ArgumentMatcher<List<*>> {
        return object : ArgumentMatcher<List<*>> {
            override fun matches(argument: List<*>): Boolean {
                return argument == expected
            }

            fun describeTo(description: Description) {
                description.appendText(expected.toString())
            }
        }
    }
}