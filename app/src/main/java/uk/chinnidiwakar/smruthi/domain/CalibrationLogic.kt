package uk.chinnidiwakar.smruthi.domain

object CalibrationEvaluator {

    fun evaluate(blocks: List<SessionSummary>): CalibrationResult {

        val sorted = blocks.sortedBy { it.nLevel }

        val recommended = sorted.lastOrNull { block ->
            val targetTotal = block.hits + block.misses
            if (targetTotal == 0) false
            else (block.hits.toFloat() / targetTotal) >= 0.7f
        }?.nLevel ?: 1

        return CalibrationResult(
            recommendedN = recommended,
            blockResults = blocks
        )
    }
}