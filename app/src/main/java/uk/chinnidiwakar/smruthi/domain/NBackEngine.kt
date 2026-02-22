package uk.chinnidiwakar.smruthi.domain

import kotlin.random.Random

private val LETTERS = listOf(
    'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L',
    'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'X', 'Z'
)

data class EngineState(
    val currentLetter: Char = ' ',
    val hits: Int = 0,
    val misses: Int = 0,
    val falseAlarms: Int = 0,
    val correctRejections: Int = 0,
    val averageHitReactionTimeMs: Long = 0,
    val averageFalseAlarmReactionTimeMs: Long = 0
)

class NBackEngine(
    private var nLevel: Int
) {
    private var stimulusStartTime: Long = 0L

    private val hitReactionTimes = mutableListOf<Long>()
    private val falseAlarmReactionTimes = mutableListOf<Long>()
    private val stimulusHistory = mutableListOf<Char>()
    private var lastLetter: Char? = null
    private var runLength: Int = 0
    private var isCurrentTarget = false
    private var userRespondedThisRound = false

    private var state = EngineState()

    fun updateNLevel(newN: Int) {
        nLevel = newN.coerceAtLeast(1)
        stimulusHistory.clear()
        isCurrentTarget = false
        userRespondedThisRound = false
    private fun applyRunLimit(candidate: Char): Char {
        // If we already repeated twice, force a different draw
        if (candidate == lastLetter && runLength >= 2) {
            val alternatives = LETTERS.filter { it != candidate }
            return alternatives.random()
        }
        return candidate
    }

    fun nextStimulus(): EngineState {
        val candidate: Char

        if (stimulusHistory.size >= nLevel && Random.nextFloat() < 0.3f) {
            // Target trial (must match N-back exactly)
            candidate = stimulusHistory[stimulusHistory.size - nLevel]
            isCurrentTarget = true
        } else {
            val forbidden =
                if (stimulusHistory.size >= nLevel)
                    stimulusHistory[stimulusHistory.size - nLevel]
                else null

            candidate = LETTERS
                .filter { it != forbidden }
                .random()

            isCurrentTarget = false
        }

// Apply perceptual run-length limit (safe for both target/non-target)
        val newLetter = applyRunLimit(candidate)

        if (newLetter == lastLetter) {
            runLength++
        } else {
            lastLetter = newLetter
            runLength = 1
        }

        stimulusStartTime = System.currentTimeMillis()
        stimulusHistory.add(newLetter)
        userRespondedThisRound = false

        state = state.copy(currentLetter = newLetter)
        return state
    }

    fun onMatchPressed(): EngineState {
        if (userRespondedThisRound) return state

        userRespondedThisRound = true
        val reactionTime = System.currentTimeMillis() - stimulusStartTime

        state = if (isCurrentTarget) {
            hitReactionTimes.add(reactionTime)
            state.copy(
                hits = state.hits + 1,
                averageHitReactionTimeMs = hitReactionTimes.average().toLong()
            )
        } else {
            falseAlarmReactionTimes.add(reactionTime)
            state.copy(
                falseAlarms = state.falseAlarms + 1,
                averageFalseAlarmReactionTimeMs = falseAlarmReactionTimes.average().toLong()
            )
        }

        return state
    }

    fun evaluateMiss(): EngineState {
        if (userRespondedThisRound) return state

        state = if (isCurrentTarget) {
            state.copy(misses = state.misses + 1)
        } else {
            state.copy(correctRejections = state.correctRejections + 1)
        }

        return state
    }
}
