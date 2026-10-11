package com.example.mycube.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycube.data.local.SolveEntity
import com.example.mycube.data.repository.SolveRepository
import com.example.mycube.domain.logic.Scrambler
import com.example.mycube.domain.model.CubeState
import com.example.mycube.domain.model.Move
import com.example.mycube.ui.cube.AnimationController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val repository: SolveRepository
) : ViewModel() {

    private val _cubeSize = MutableStateFlow(3)
    val cubeSize = _cubeSize.asStateFlow()

    private val _cubeState = MutableStateFlow(CubeState.createStandard(3))
    val cubeState = _cubeState.asStateFlow()

    private val _timeMillis = MutableStateFlow(0L)
    val timeMillis = _timeMillis.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private val _moveCount = MutableStateFlow(0)
    val moveCount = _moveCount.asStateFlow()

    private val _scramble = MutableStateFlow("")
    val scramble = _scramble.asStateFlow()

    private val moveHistory = Stack<Move>()
    private val redoStack = Stack<Move>()

    private var timerJob: Job? = null
    private var startTime = 0L

    val animationController = AnimationController()
    private var isAnimating = false

    init {
        generateNewScramble()
    }

    fun setCubeSize(size: Int) {
        _cubeSize.value = size
        _cubeState.value = CubeState.createStandard(size)
        resetSession()
    }

    fun generateNewScramble() {
        if (isAnimating) return
        val newScramble = Scrambler.generateScramble(_cubeSize.value)
        _scramble.value = newScramble.joinToString(" ")
        var newState = CubeState.createStandard(_cubeSize.value)
        newScramble.forEach { newState = newState.applyMove(it) }
        _cubeState.value = newState
        resetTimer()
    }

    fun applyMove(move: Move) {
        if (isAnimating) {
            Log.d("PlayViewModel", "Move ignored, already animating: $move")
            return
        }
        Log.d("PlayViewModel", "Applying move: $move")
        viewModelScope.launch {
            try {
                isAnimating = true
                if (!_isTimerRunning.value && moveCount.value == 0) {
                    startTimer()
                }
                
                animationController.animateMove(move)
                
                _cubeState.value = _cubeState.value.applyMove(move)
                _moveCount.value++
                moveHistory.push(move)
                redoStack.clear()

                if (_cubeState.value.isSolved() && _isTimerRunning.value) {
                    stopTimer()
                    saveSolve()
                }
            } catch (e: Exception) {
                Log.e("PlayViewModel", "Error applying move", e)
            } finally {
                isAnimating = false
            }
        }
    }

    fun undo() {
        if (isAnimating) return
        viewModelScope.launch {
            if (moveHistory.isNotEmpty()) {
                try {
                    isAnimating = true
                    val move = moveHistory.pop()
                    val reverseMove = move.reverse()
                    animationController.animateMove(reverseMove)
                    _cubeState.value = _cubeState.value.applyMove(reverseMove)
                    _moveCount.value--
                    redoStack.push(move)
                } finally {
                    isAnimating = false
                }
            }
        }
    }

    fun redo() {
        if (isAnimating) return
        viewModelScope.launch {
            if (redoStack.isNotEmpty()) {
                try {
                    isAnimating = true
                    val move = redoStack.pop()
                    animationController.animateMove(move)
                    _cubeState.value = _cubeState.value.applyMove(move)
                    _moveCount.value++
                    moveHistory.push(move)
                } finally {
                    isAnimating = false
                }
            }
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        startTime = System.currentTimeMillis() - _timeMillis.value
        timerJob = viewModelScope.launch {
            while (true) {
                _timeMillis.value = System.currentTimeMillis() - startTime
                delay(10)
            }
        }
    }

    fun stopTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        stopTimer()
        _timeMillis.value = 0
        _moveCount.value = 0
        moveHistory.clear()
        redoStack.clear()
    }

    private fun resetSession() {
        resetTimer()
        generateNewScramble()
    }

    private fun saveSolve() {
        viewModelScope.launch {
            val solve = SolveEntity(
                timeMillis = _timeMillis.value,
                movesCount = _moveCount.value,
                movesSequence = moveHistory.joinToString(" "),
                scramble = _scramble.value,
                cubeSize = _cubeSize.value
            )
            repository.saveSolve(solve)
        }
    }
}
