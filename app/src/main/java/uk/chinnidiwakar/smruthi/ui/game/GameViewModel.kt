package uk.chinnidiwakar.smruthi.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.chinnidiwakar.smruthi.domain.NBackEngine
import uk.chinnidiwakar.smruthi.domain.PerformanceEvaluator
import uk.chinnidiwakar.smruthi.domain.SessionSummary

class GameViewModel : ViewModel() {

    private lateinit var engine: NBackEngine
    private var duration: Int = 60
    private var sessionSummary: SessionSummary? = null

    fun getSessionSummary(): SessionSummary? = sessionSummary

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState
    private val _finishEvent = MutableStateFlow(false)
    val finishEvent: StateFlow<Boolean> = _finishEvent

    private var nLevel = 2

    fun startGame(
        n: Int,
        durationSeconds: Int? = null,
        trialCount: Int? = null,
        stimulusIntervalMs: Int = 1000,
        adaptiveEnabled: Boolean = false,
        adaptiveBlockSize: Int = 20
    ) {

        nLevel = n
        this.duration = durationSeconds ?: 60

        engine = NBackEngine(n)

        _finishEvent.value = false
        _uiState.value = GameUiState(currentNLevel = nLevel)

        viewModelScope.launch {

            var stimuliSinceAdaptiveCheck = 0

            if (trialCount != null) {
                repeat(trialCount) {
                    generateStimulus()
                    delay(stimulusIntervalMs.toLong())
                    evaluateMiss()
                    stimuliSinceAdaptiveCheck++

                    if (adaptiveEnabled && stimuliSinceAdaptiveCheck >= adaptiveBlockSize) {
                        adaptDifficultyIfNeeded()
                        stimuliSinceAdaptiveCheck = 0
                    }
                }

                finishSession()
                return@launch
            }

            val startTime = System.currentTimeMillis()

            while (true) {

                val elapsed = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                val remaining = duration - elapsed

                if (remaining <= 0) {
                    finishSession()
                    break
                }

                generateStimulus()

                _uiState.value = _uiState.value.copy(timeRemaining = remaining)

                delay(stimulusIntervalMs.toLong())
                evaluateMiss()
                stimuliSinceAdaptiveCheck++

                if (adaptiveEnabled && stimuliSinceAdaptiveCheck >= adaptiveBlockSize) {
                    adaptDifficultyIfNeeded()
                    stimuliSinceAdaptiveCheck = 0
                }
            }
        }
    }

    private fun adaptDifficultyIfNeeded() {
        val current = _uiState.value
        val recommendation = PerformanceEvaluator.evaluate(
            currentN = nLevel,
            hits = current.hits,
            misses = current.misses,
            falseAlarms = current.falseAlarms,
            correctRejections = current.correctRejections,
            avgHitRt = current.averageHitReactionTimeMs
        )

        if (recommendation.suggestedN != nLevel) {
            nLevel = recommendation.suggestedN
            engine.updateNLevel(nLevel)
            _uiState.value = _uiState.value.copy(currentNLevel = nLevel)
        }
    }

    private fun finishSession() {

        sessionSummary = SessionSummary(
            nLevel = nLevel,
            hits = _uiState.value.hits,
            misses = _uiState.value.misses,
            falseAlarms = _uiState.value.falseAlarms,
            correctRejections = _uiState.value.correctRejections,
            averageHitReactionTimeMs = _uiState.value.averageHitReactionTimeMs,
            averageFalseAlarmReactionTimeMs = _uiState.value.averageFalseAlarmReactionTimeMs
        )

        _finishEvent.value = true
    }

    private fun generateStimulus() {

        val engineState = engine.nextStimulus()

        _uiState.value = _uiState.value.copy(
            currentLetter = engineState.currentLetter.toString(),
            currentNLevel = nLevel,
            hits = engineState.hits,
            misses = engineState.misses,
            falseAlarms = engineState.falseAlarms,
            correctRejections = engineState.correctRejections,
            averageHitReactionTimeMs = engineState.averageHitReactionTimeMs,
            averageFalseAlarmReactionTimeMs = engineState.averageFalseAlarmReactionTimeMs
        )
    }

    fun onMatchPressed() {

        val engineState = engine.onMatchPressed()

        _uiState.value = _uiState.value.copy(
            currentLetter = engineState.currentLetter.toString(),
            currentNLevel = nLevel,
            hits = engineState.hits,
            misses = engineState.misses,
            falseAlarms = engineState.falseAlarms,
            correctRejections = engineState.correctRejections,
            averageHitReactionTimeMs = engineState.averageHitReactionTimeMs,
            averageFalseAlarmReactionTimeMs = engineState.averageFalseAlarmReactionTimeMs
        )
    }

    private fun evaluateMiss() {

        val engineState = engine.evaluateMiss()

        _uiState.value = _uiState.value.copy(
            currentLetter = engineState.currentLetter.toString(),
            currentNLevel = nLevel,
            hits = engineState.hits,
            misses = engineState.misses,
            falseAlarms = engineState.falseAlarms,
            correctRejections = engineState.correctRejections,
            averageHitReactionTimeMs = engineState.averageHitReactionTimeMs,
            averageFalseAlarmReactionTimeMs = engineState.averageFalseAlarmReactionTimeMs
        )
    }
}
