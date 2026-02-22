package uk.chinnidiwakar.smruthi.domain

class PerformanceHistory(
    private val maxSessions: Int = 5
) {

    private val sessions = mutableListOf<SessionSummary>()

    fun addSession(summary: SessionSummary) {
        sessions.add(summary)
        if (sessions.size > maxSessions) {
            sessions.removeFirst()
        }
    }

    fun getSessions(): List<SessionSummary> = sessions.toList()

    fun getAverageAccuracy(): Float {
        if (sessions.isEmpty()) return 0f

        return sessions.map {
            val targetTotal = it.hits + it.misses
            if (targetTotal == 0) 0f
            else it.hits.toFloat() / targetTotal
        }.average().toFloat()
    }

    fun getAverageHitRT(): Long {
        if (sessions.isEmpty()) return 0
        return sessions.map { it.averageHitReactionTimeMs }
            .average()
            .toLong()
    }
}