package com.example.expensetracker.dtos

data class SMSMessageDTO(
    val address: String,
    val body: String,
    val time: Long
)
