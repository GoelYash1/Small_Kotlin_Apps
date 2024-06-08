package com.example.eggcounter

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class EggCounterViewModel: ViewModel() {
    private var eggCount = 0
    private val eggColors = listOf(Color.Red, Color.Gray, Color.Blue,Color.Cyan,Color.Magenta)

    private val _currentEggColor = mutableStateOf(Color.Black)
    val currentEggColor: State<Color> = _currentEggColor

    private val _currentEggCount = mutableStateOf(0)
    val currentEggCount:State<Int> = _currentEggCount

    fun onEggClick(){
        eggCount++
        _currentEggCount.value = eggCount
        _currentEggColor.value = eggColors.random()
    }
}