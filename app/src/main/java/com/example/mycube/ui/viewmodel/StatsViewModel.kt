package com.example.mycube.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mycube.data.local.SolveEntity
import com.example.mycube.data.repository.SolveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: SolveRepository
) : ViewModel() {

    private val _selectedSize = MutableStateFlow(3)
    val selectedSize = _selectedSize.asStateFlow()

    val solves = _selectedSize.flatMapLatest { size ->
        repository.getSolvesBySize(size)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val bestTime = solves.map { list ->
        list.minByOrNull { it.timeMillis }?.timeMillis
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val averageOf5 = solves.map { list ->
        calculateAoN(list.take(5), 5)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val averageOf12 = solves.map { list ->
        calculateAoN(list.take(12), 12)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun setCubeSize(size: Int) {
        _selectedSize.value = size
    }

    fun deleteSolve(solve: SolveEntity) {
        viewModelScope.launch {
            repository.deleteSolve(solve)
        }
    }

    private fun calculateAoN(solves: List<SolveEntity>, n: Int): Long? {
        if (solves.size < n) return null
        val times = solves.map { it.timeMillis }.sorted()
        // Remove best and worst
        val filtered = times.subList(1, times.size - 1)
        return filtered.average().toLong()
    }
}
