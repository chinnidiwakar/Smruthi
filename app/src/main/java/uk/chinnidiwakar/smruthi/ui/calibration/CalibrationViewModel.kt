package uk.chinnidiwakar.smruthi.ui.calibration

import androidx.lifecycle.ViewModel
import uk.chinnidiwakar.smruthi.domain.CalibrationEvaluator
import uk.chinnidiwakar.smruthi.domain.CalibrationResult
import uk.chinnidiwakar.smruthi.domain.SessionSummary

class CalibrationViewModel : ViewModel() {

    private val blocks = listOf(1, 2, 3)
    private val results = mutableListOf<SessionSummary>()

    var currentBlockIndex = 0
        private set

    var calibrationResult: CalibrationResult? = null
        private set

    fun getCurrentN(): Int = blocks[currentBlockIndex]

    fun onBlockCompleted(summary: SessionSummary): Boolean {

        results.add(summary)
        currentBlockIndex++

        return if (currentBlockIndex >= blocks.size) {
            calibrationResult = CalibrationEvaluator.evaluate(results)
            true // calibration finished
        } else {
            false // continue next block
        }
    }
}