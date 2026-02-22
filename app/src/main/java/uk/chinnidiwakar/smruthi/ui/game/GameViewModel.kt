package uk.chinnidiwakar.smruthi.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import uk.chinnidiwakar.smruthi.domain.NBackEngine
import uk.chinnidiwakar.smruthi.domain.PerformanceHistory
import uk.chinnidiwakar.smruthi.domain.SessionSummary

private val LETTERS = listOf(
    'B','C','D','F','G','H','J','K','L',
    'M','N','P','Q','R','S','T','V','X','Z'
)


class GameViewModel : ViewModel() {

    private lateinit var engine: NBackEngine
    private var duration: Int = 60
    private var trialCount: Int? = null
    private var sessionSummary: SessionSummary? = null

    fun getSessionSummary(): SessionSummary? = sessionSummary

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState
    private val _finishEvent = MutableStateFlow(false)
    val finishEvent: StateFlow<Boolean> = _finishEvent

    private val stimulusHistory = mutableListOf<Char>()

    private var nLevel = 2

    private var isCurrentTarget = false
    private var userRespondedThisRound = false
    private val performanceHistory = PerformanceHistory()


    fun startGame(
        n: Int,
        durationSeconds: Int? = null,
        trialCount: Int? = null
    ) {

        nLevel = n
        this.duration = durationSeconds ?: 60
        this.trialCount = trialCount

        stimulusHistory.clear()
        engine = NBackEngine(n)

        _finishEvent.value = false
        _uiState.value = GameUiState()

        viewModelScope.launch {

            if (trialCount != null) {

                repeat(trialCount) {

                    generateStimulus()
                    delay(1000)
                    evaluateMiss()
                }

                finishSession()
                return@launch
            }

            val startTime = System.currentTimeMillis()

            while (true) {

                val elapsed =
                    ((System.currentTimeMillis() - startTime) / 1000).toInt()

                val remaining = duration - elapsed

                if (remaining <= 0) {
                    finishSession()
                    break
                }

                generateStimulus()

                _uiState.value = _uiState.value.copy(
                    timeRemaining = remaining
                )

                delay(1000)
                evaluateMiss()
            }
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

        performanceHistory.addSession(sessionSummary!!)
        _finishEvent.value = true
    }

    private fun generateStimulus() {

        val engineState = engine.nextStimulus()

        _uiState.value = _uiState.value.copy(
            currentLetter = engineState.currentLetter.toString(),
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
            hits = engineState.hits,
            misses = engineState.misses,
            falseAlarms = engineState.falseAlarms,
            correctRejections = engineState.correctRejections,
            averageHitReactionTimeMs = engineState.averageHitReactionTimeMs,
            averageFalseAlarmReactionTimeMs = engineState.averageFalseAlarmReactionTimeMs
        )
    }
}