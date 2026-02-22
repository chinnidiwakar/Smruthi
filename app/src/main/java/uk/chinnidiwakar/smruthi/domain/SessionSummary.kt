package uk.chinnidiwakar.smruthi.domain

data class SessionSummary(
    val nLevel: Int,
    val hits: Int,
    val misses: Int,
    val falseAlarms: Int,
    val correctRejections: Int,
    val averageHitReactionTimeMs: Long,
    val averageFalseAlarmReactionTimeMs: Long
)