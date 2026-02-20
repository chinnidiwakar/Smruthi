package uk.chinnidiwakar.smruthi.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import uk.chinnidiwakar.smruthi.domain.NBackEngine

private val LETTERS = listOf(
    'B','C','D','F','G','H','J','K','L',
    'M','N','P','Q','R','S','T','V','X','Z'
)
private lateinit var engine: NBackEngine

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState
    private val _finishEvent = MutableStateFlow(false)
    val finishEvent: StateFlow<Boolean> = _finishEvent

    private val stimulusHistory = mutableListOf<Char>()

    private var nLevel = 2
    private var duration = 60

    private var isCurrentTarget = false
    private var userRespondedThisRound = false

    fun startGame(n: Int, durationSeconds: Int) {
        nLevel = n
        duration = durationSeconds
        stimulusHistory.clear()
        engine = NBackEngine(n)

        viewModelScope.launch {

            val startTime = System.currentTimeMillis()

            while (true) {

                val elapsed =
                    ((System.currentTimeMillis() - startTime) / 1000).toInt()

                val remaining = duration - elapsed
                if (remaining <= 0) {
                    _finishEvent.value = true
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

    private fun generateStimulus() {

        val engineState = engine.nextStimulus()

        _uiState.value = _uiState.value.copy(
            currentLetter = engineState.currentLetter.toString(),
            hits = engineState.hits,
            misses = engineState.misses,
            falseAlarms = engineState.falseAlarms,
            correctRejections = engineState.correctRejections
        )
    }

    fun onMatchPressed() {

        val engineState = engine.onMatchPressed()

        _uiState.value = _uiState.value.copy(
            hits = engineState.hits,
            falseAlarms = engineState.falseAlarms
        )
    }

    private fun evaluateMiss() {

        val engineState = engine.evaluateMiss()

        _uiState.value = _uiState.value.copy(
            misses = engineState.misses,
            correctRejections = engineState.correctRejections
        )
    }
}