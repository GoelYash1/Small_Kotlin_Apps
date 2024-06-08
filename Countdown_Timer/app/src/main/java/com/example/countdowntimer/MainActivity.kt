package com.example.countdowntimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.countdowntimer.ui.theme.CountdownTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountdownTimerTheme {
                val countdownTimerViewModel = viewModel<CountdownTimerViewModel>()
                CountDownTimerScreen(countdownTimerViewModel)
            }
        }
    }
}

@Composable
fun CountDownTimerScreen(countdownTimerViewModel: CountdownTimerViewModel) {
    var duration by remember {
        mutableStateOf("0000")
    }
    var canClickNumbers by remember {
        mutableStateOf(false)
    }
    var stopButtonClickCount by remember {
        mutableStateOf(0)
    }

    val getStopButtonClickCount by countdownTimerViewModel.buttonClickCount.collectAsState()
    val buttonEnabled by countdownTimerViewModel.buttonEnabled.collectAsState()
    val timerValue by countdownTimerViewModel.timerValue.collectAsState()

    LaunchedEffect(timerValue) {
        duration = timerValue
    }
    LaunchedEffect(getStopButtonClickCount) {
        stopButtonClickCount = getStopButtonClickCount
    }
    LaunchedEffect(buttonEnabled){
        canClickNumbers = buttonEnabled
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Count Down Timer",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Cursive,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimeBox(duration, "Minutes")
            }
            Spacer(modifier = Modifier.padding(5.dp))
            Box(modifier = Modifier
                .padding(10.dp)
            ) {
                Text(
                    text = ":",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.offset(y = (-20).dp)
                )
            }
            Spacer(modifier = Modifier.padding(5.dp))
            Column(
                modifier = Modifier
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimeBox(duration, "Seconds")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TimerButtonsGrid(countdownTimerViewModel,canClickNumbers,stopButtonClickCount)

    }
}

@Composable
fun TimerButtonsGrid(
    countdownTimerViewModel: CountdownTimerViewModel,
    canClickNumbers: Boolean,
    stopButtonClickCount: Int
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
    ) {
        items(12) { number ->
            TimerButton(
                symbol =
                if((number-2) == -1){
                    if( stopButtonClickCount == 0){
                        "X"
                    }
                    else{
                        "---"
                    }
                }
                else
                {
                    (number-2).toString()
                },
                modifier = Modifier
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .size(100.dp),
                onClick = {
                    if(canClickNumbers || (number-2) < 0){
                        countdownTimerViewModel.onButtonClicked((number-2).toString())
                    }
                }
            )
        }


    }
}

@Composable
fun TimerButton(
    symbol: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clickable { onClick() }
            .then(modifier)
    ) {
        Text(
            text = if(symbol=="-2") "√" else symbol,
            fontSize = 24.sp,
            color = Color.White
        )
    }
}


@Composable
fun TimeBox(duration: String, timeType: String) {
    val formattedDuration = duration.padStart(4, '0')

    Row {
        Box(
            modifier = Modifier
                .border(1.dp, Color.Black)
                .height(80.dp)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (timeType == "Minutes") formattedDuration[0].toString()  else formattedDuration[2].toString(),
                fontSize = 36.sp
            )
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Box(
            modifier = Modifier
                .border(1.dp, Color.Black)
                .height(80.dp)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (timeType == "Minutes") formattedDuration[1].toString() else formattedDuration[3].toString(),
                fontSize = 36.sp
            )
        }
    }
    Row {
        Text(
            text = timeType,
            fontSize = 24.sp
        )
    }
}
