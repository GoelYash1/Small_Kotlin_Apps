package com.example.expensetracker.screens.mainScreen.transactions

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.expensetracker.data.models.Transaction
import com.example.expensetracker.data.models.TransactionCategories
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionItemUI(transaction: Transaction) {
    val showTransactionDetailsScreen = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                showTransactionDetailsScreen.value = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Show the icon associated with the category
        val category =
            TransactionCategories.categories.find { it.name == transaction.categoryName }
        category?.let {
            Icon(
                painter = painterResource(id = it.iconResId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = transaction.otherPartyName,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column {
            Text(
                text = transaction.amount.toString(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = if (transaction.amount >= 0) Color.Green else Color.Red
            )
            Text(
                text = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(transaction.timestamp),
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            )
        }
        if (showTransactionDetailsScreen.value) {

        }
    }

}