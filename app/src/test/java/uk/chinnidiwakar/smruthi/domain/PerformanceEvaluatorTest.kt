package uk.chinnidiwakar.smruthi.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PerformanceEvaluatorTest {

    @Test
    fun doesNotIncreaseDifficultyWhenNoHits() {
        val rec = PerformanceEvaluator.evaluate(
            currentN = 3,
            hits = 0,
            misses = 0,
            falseAlarms = 0,
            correctRejections = 10,
            avgHitRt = 0
        )

        assertEquals(3, rec.suggestedN)
    }

    @Test
    fun capsIncreaseAtMaxSupportedLevel() {
        val rec = PerformanceEvaluator.evaluate(
            currentN = 5,
            hits = 12,
            misses = 1,
            falseAlarms = 0,
            correctRejections = 10,
            avgHitRt = 500
        )

        assertEquals(5, rec.suggestedN)
    }
}
