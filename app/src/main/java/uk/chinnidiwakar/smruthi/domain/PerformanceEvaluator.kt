package uk.chinnidiwakar.smruthi.domain

data class DifficultyRecommendation(
    val suggestedN: Int,
    val message: String
)

object PerformanceEvaluator {

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

        return when {

            accuracy >= 0.85f &&
                    falseRate <= 0.15f &&
                    avgHitRt < 900 ->
                DifficultyRecommendation(
                    suggestedN = currentN + 1,
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
                    suggestedN = currentN,
                    message = "Training within optimal range. Maintain level."
                )
        }
    }
}