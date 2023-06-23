package com.example.expensetracker.api

import android.content.ContentResolver
import android.net.Uri
import com.example.expensetracker.dtos.SMSMessageDTO

class SMSReadAPI(private val contentResolver: ContentResolver) {
    fun getAllSms(from: Long?, to: Long?): List<SMSMessageDTO> {
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

            smsMessages.add(SMSMessageDTO(address, body, time))
        }

        cursor.close()

        return smsMessages
    }
}

