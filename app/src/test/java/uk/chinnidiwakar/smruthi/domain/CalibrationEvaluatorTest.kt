package uk.chinnidiwakar.smruthi.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class CalibrationEvaluatorTest {

    @Test
    fun picksHighestLevelThatPassesAccuracyAndFalseAlarmThresholds() {
        val blocks = listOf(
            SessionSummary(1, hits = 12, misses = 2, falseAlarms = 1, correctRejections = 10, 0, 0),
            SessionSummary(2, hits = 10, misses = 3, falseAlarms = 2, correctRejections = 8, 0, 0),
            SessionSummary(3, hits = 8, misses = 5, falseAlarms = 5, correctRejections = 5, 0, 0)
        )

        val result = CalibrationEvaluator.evaluate(blocks)

        assertEquals(2, result.recommendedN)
    }

    @Test
    fun fallsBackToBestBalancedScoreWhenNoBlockPassesHardThresholds() {
        val blocks = listOf(
            SessionSummary(1, hits = 5, misses = 4, falseAlarms = 5, correctRejections = 5, 0, 0),
            SessionSummary(2, hits = 6, misses = 5, falseAlarms = 4, correctRejections = 6, 0, 0),
            SessionSummary(3, hits = 4, misses = 7, falseAlarms = 3, correctRejections = 7, 0, 0)
        )

        val result = CalibrationEvaluator.evaluate(blocks)

        assertEquals(2, result.recommendedN)
    }
}
