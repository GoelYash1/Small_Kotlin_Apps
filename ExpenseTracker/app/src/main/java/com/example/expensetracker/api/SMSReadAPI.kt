package com.example.expensetracker.api

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.expensetracker.data.dtos.SMSMessageDTO
import com.example.expensetracker.helper.TransactionSMSFilter
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class SMSReadAPI(private val contentResolver: ContentResolver) {
    private fun getAllSms(from: Long?, to: Long?): List<SMSMessageDTO> {
        val smsMessages = mutableListOf<SMSMessageDTO>()

        val projections = arrayOf("_id", "address", "body", "person", "date")
        val selection = if (from != null && to != null) "date >= ? AND date <= ?" else null
        val selectionArgs = if (from != null && to != null) arrayOf(from.toString(), to.toString()) else null
        val sortOrder = "date DESC"

        val cursor = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            projections,
            selection,
            selectionArgs,
            sortOrder
        ) ?: return emptyList()

        while (cursor.moveToNext()) {
            val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
            val body = cursor.getString(cursor.getColumnIndexOrThrow("body"))
            val time = cursor.getLong(cursor.getColumnIndexOrThrow("date"))

            val amountSpent = TransactionSMSFilter().getAmountSpent(body)
            val isExpense = TransactionSMSFilter().isExpense(body)
            val isIncome = TransactionSMSFilter().isIncome(body)

            if (amountSpent != null && (isExpense || isIncome)) {
                smsMessages.add(SMSMessageDTO(address, body, time))
            }
        }

        cursor.close()

        return smsMessages
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getGroupedSMSMessagesByDateMonthYear(year: Int? = null, month: Month? = null, date: Int? = null): Map<String, List<SMSMessageDTO>> {
        val startDateTime: LocalDateTime
        val endDateTime: LocalDateTime

        if (year != null && month != null && date != null) {
            startDateTime = LocalDateTime.of(year, month, date, 0, 0, 0)
            endDateTime = LocalDateTime.of(year, month, date, 23, 59, 59)
        } else if (year != null && month != null) {
            startDateTime = LocalDateTime.of(year, month, 1, 0, 0, 0)
            val lastDayOfMonth = startDateTime.month.length(Year.isLeap(year.toLong()))
            endDateTime = LocalDateTime.of(year, month, lastDayOfMonth, 23, 59, 59)
        } else if (year != null) {
            startDateTime = LocalDateTime.of(year, 1, 1, 0, 0, 0)
            endDateTime = LocalDateTime.of(year, 12, 31, 23, 59, 59)
        } else {
            // Default: Retrieve all SMS messages
            val currentYear = Year.now().value
            startDateTime = LocalDateTime.of(currentYear, 1, 1, 0, 0, 0)
            endDateTime = LocalDateTime.of(currentYear, 12, 31, 23, 59, 59)
        }

        val startMillis = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val allSMSMessages = getAllSms(startMillis, endMillis)

        return allSMSMessages.groupBy { sms ->
            val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(sms.time), ZoneId.systemDefault())
            val formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()))
            formattedDate
        }
    }
}

