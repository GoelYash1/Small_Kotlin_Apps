package com.example.expensetracker.screens.mainScreen.transactions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.api.SMSReadAPI
import com.example.expensetracker.helper.TransactionSMSFilter
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(){
    val contentResolver = LocalContext.current.contentResolver
    val smsReadAPI = SMSReadAPI(contentResolver)
    val transactionSMS = smsReadAPI.getGroupedSMSMessagesByDate()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        transactionSMS.forEach { (date, messages) ->
            stickyHeader {
                Text(
                    text = date,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray)
                        .padding(8.dp)
                )
            }
            items(messages) { sms ->
                Box(modifier = Modifier.padding(5.dp)) {
                    Column {
                        // Convert timestamp to readable format
                        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(sms.time), ZoneId.systemDefault())
                        val formattedTime = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault()))

                        Text(
                            text = formattedTime,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = sms.body,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}