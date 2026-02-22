package uk.chinnidiwakar.smruthi.domain

object CalibrationEvaluator {

    private const val MIN_TARGET_TRIALS = 8
    private const val TARGET_ACCURACY_THRESHOLD = 0.70f
    private const val FALSE_ALARM_THRESHOLD = 0.30f

    fun evaluate(blocks: List<SessionSummary>): CalibrationResult {

        val sorted = blocks.sortedBy { it.nLevel }

        val recommended = sorted
            .lastOrNull { block ->
                val targetTotal = block.hits + block.misses
                val nonTargetTotal = block.falseAlarms + block.correctRejections

                val targetAccuracy =
                    if (targetTotal > 0) block.hits.toFloat() / targetTotal
                    else 0f

                val falseAlarmRate =
                    if (nonTargetTotal > 0) block.falseAlarms.toFloat() / nonTargetTotal
                    else 0f

                targetTotal >= MIN_TARGET_TRIALS &&
                    targetAccuracy >= TARGET_ACCURACY_THRESHOLD &&
                    falseAlarmRate <= FALSE_ALARM_THRESHOLD
            }
            ?.nLevel
            ?: sorted.maxByOrNull { block ->
                val targetTotal = block.hits + block.misses
                val nonTargetTotal = block.falseAlarms + block.correctRejections

                val targetAccuracy =
                    if (targetTotal > 0) block.hits.toFloat() / targetTotal
                    else 0f

                val falseAlarmRate =
                    if (nonTargetTotal > 0) block.falseAlarms.toFloat() / nonTargetTotal
                    else 0f

                (targetAccuracy * 0.7f) + ((1f - falseAlarmRate) * 0.3f)
            }
            ?.nLevel
            ?: 1

        return CalibrationResult(
            recommendedN = recommended,
            blockResults = blocks
        )
    }
}
