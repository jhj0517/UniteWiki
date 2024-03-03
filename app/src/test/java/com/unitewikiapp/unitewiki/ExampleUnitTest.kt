package com.unitewikiapp.unitewiki

import org.junit.Assert
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCountSelectedSkills() {
        // Arrange
        val reviewsList = listOf(
            "1,3",
            "1,3",
            "2,3",
            "2,3",
            "2,3",
            "1,4",
            "1,4"
        )
        val expectedCounts = intArrayOf(4, 3, 5, 2)
        val result = countSelectedSkills(reviewsList)

        Assert.assertArrayEquals(expectedCounts, result)
    }

    fun countSelectedSkills(reviews: List<String>): IntArray {
        val counts = IntArray(4)

        for (review in reviews) {
            val parts = review.split(",").map { it.toInt() }
            if (parts.size == 2) {
                val firstNum = parts[0]
                val secondNum = parts[1]

                if (firstNum in 1..4) counts[firstNum - 1]++
                if (secondNum in 1..4) counts[secondNum - 1]++
            }
        }

        return counts
    }

}