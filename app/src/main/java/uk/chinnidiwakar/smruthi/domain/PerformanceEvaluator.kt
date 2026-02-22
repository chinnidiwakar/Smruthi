package uk.chinnidiwakar.smruthi.domain

data class DifficultyRecommendation(
    val suggestedN: Int,
    val message: String
)

object PerformanceEvaluator {

    private const val MAX_SUGGESTED_N = 5

    fun evaluate(
        currentN: Int,
        hits: Int,
        misses: Int,
        falseAlarms: Int,
        correctRejections: Int,
        avgHitRt: Long
    ): DifficultyRecommendation {

        val targetTotal = hits + misses
        val nonTargetTotal = falseAlarms + correctRejections

        val accuracy =
            if (targetTotal > 0)
                hits.toFloat() / targetTotal
            else 0f

        val falseRate =
            if (nonTargetTotal > 0)
                falseAlarms.toFloat() / nonTargetTotal
            else 0f

        val canIncrease =
            hits > 0 &&
                accuracy >= 0.85f &&
                falseRate <= 0.15f &&
                avgHitRt in 250..900

        return when {

            canIncrease ->
                DifficultyRecommendation(
                    suggestedN = (currentN + 1).coerceAtMost(MAX_SUGGESTED_N),
                    message = "Performance strong. Consider increasing difficulty."
                )

            accuracy < 0.60f ||
                falseRate > 0.35f ->
                DifficultyRecommendation(
                    suggestedN = (currentN - 1).coerceAtLeast(1),
                    message = "Cognitive load too high. Consider lowering difficulty."
                )

            else ->
                DifficultyRecommendation(
                    suggestedN = currentN.coerceIn(1, MAX_SUGGESTED_N),
                    message = "Training within optimal range. Maintain level."
                )
        }
    }
}
