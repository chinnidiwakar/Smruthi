package uk.chinnidiwakar.smruthi.ui.game

data class GameUiState(
    val currentLetter: String = "",
    val timeRemaining: Int = 0,
    val hits: Int = 0,
    val misses: Int = 0,
    val falseAlarms: Int = 0,
    val correctRejections: Int = 0
)