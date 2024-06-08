package com.example.eggcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eggcounter.ui.theme.EggCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EggCounterTheme {
                val eggCounterViewModel = viewModel<EggCounterViewModel>()
                EggCounter(eggCounterViewModel)
            }
        }
    }
}

@Composable
fun EggCounter(eggCounterViewModel: EggCounterViewModel) {
    val eggColor = eggCounterViewModel.currentEggColor
    val eggCount = eggCounterViewModel.currentEggCount
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(id = R.drawable.ic_egg),
                contentDescription = null,
                colorFilter = ColorFilter.tint(eggColor.value),
                modifier = Modifier
                    .clickable {
                        eggCounterViewModel.onEggClick()
                    }
                    .height(300.dp)
                    .width(300.dp)
            )
            Spacer(modifier = Modifier.padding(40.dp))
            Text(
                text = "The Egg has been clicked",
                fontSize = 24.sp
            )
            Text(
                text = eggCount.value.toString(),
                fontSize = 40.sp,
                color = eggColor.value
            )
            Text(text = "Times", fontSize = 24.sp)
        }
    }
}
