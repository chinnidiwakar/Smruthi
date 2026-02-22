package uk.chinnidiwakar.smruthi.domain

data class CalibrationResult(
    val recommendedN: Int,
    val blockResults: List<SessionSummary>
)