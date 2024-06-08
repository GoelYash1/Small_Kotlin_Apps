package com.example.countdowntimer

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CountdownTimerViewModel: ViewModel() {
    private var countDownTimer: CountDownTimer? = null
    private var _timerValue = MutableStateFlow("")
    val timerValue = _timerValue
    private var _buttonEnabled = MutableStateFlow(true)
    val buttonEnabled = _buttonEnabled
    private var _stopButtonClickCount = MutableStateFlow(0)
    val buttonClickCount = _stopButtonClickCount

    private fun startTimer(durationInMillis: Long) {
        if (durationInMillis <= 0) {
            _buttonEnabled.value = true
            return
        }
        _stopButtonClickCount.value = 0
        _buttonEnabled.value = false
        countDownTimer?.cancel() // Cancel any existing timer
        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisLeft: Long) {
                val seconds = millisLeft / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60

                _timerValue.value = String.format("%02d%02d", minutes, remainingSeconds)
            }


            override fun onFinish() {
                _timerValue.value = ""
                _buttonEnabled.value = true
                _stopButtonClickCount.value = 0
                playSound()
            }
        }
        countDownTimer?.start()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        _timerValue.value = ""
        _buttonEnabled.value = true
        _stopButtonClickCount.value = 0
    }

    private fun stopTimer(){
        countDownTimer?.cancel()
    }

    fun onButtonClicked(number: String) {
        when (number) {
            "-2" -> {
                _timerValue.value = _timerValue.value.padStart(4, '0')
                val totalValue = _timerValue.value.substring(0, 2).toInt() * 60 +
                        _timerValue.value.substring(2, 4).toInt()

                startTimer(totalValue.toLong() * 1000L)
            }
            "-1" -> {
                if (_stopButtonClickCount.value == 0) {
                    stopTimer()
                } else if (_stopButtonClickCount.value == 1) {
                    resetTimer()
                }
                _stopButtonClickCount.value++
            }
            else -> updateDuration(number)
        }
    }

    private fun playSound(){

    }

    private fun updateDuration(number: String) {
        if(_timerValue.value.length>4){
            return
        }
        _timerValue.value+=number
        Log.d("timer value", _timerValue.value.length.toString())
    }

}
