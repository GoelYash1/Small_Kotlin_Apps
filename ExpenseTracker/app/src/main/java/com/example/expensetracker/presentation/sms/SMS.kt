package com.example.expensetracker.presentation.sms

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.api.SMSReadAPI
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SMS() {
    val year = 2023
    val month = Month.MAY
    val startOfMonth = LocalDate.of(year, month, 1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    val endOfMonth = LocalDate.of(year, month, Month.of(month.value).length(Year.isLeap(year.toLong()))).atTime(23, 59, 59)
        .toInstant(ZoneOffset.UTC).toEpochMilli()

    val allSMSMessages =
        SMSReadAPI(LocalContext.current.contentResolver).getAllSms(startOfMonth, endOfMonth)

    // Group messages by date
    val groupedSMS = allSMSMessages.groupBy {
        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it.time), ZoneId.systemDefault())
        val formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
        formattedDate
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        groupedSMS.forEach { (date, messages) ->
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




