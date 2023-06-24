package com.example.expensetracker.helper

import java.util.regex.Pattern

class TransactionSMSFilter {
    companion object {
        const val DEBIT_PATTERN = "debited|debit|deducted"
        const val MISC_PATTERN = "payment|spent|paying|sent|UPI"
        const val CREDIT_PATTERN = "credited | credit | added"

        private val IGNORED_WORDS = listOf("redeem", "offer", "rewards", "voucher", "win", "congratulations", "getting")
    }

    fun isExpense(message: String): Boolean {
        val regex =
            "(?=.*[Aa]ccount.*|.*[Aa]/[Cc].*|.*[Aa][Cc][Cc][Tt].*|.*[Cc][Aa][Rr][Dd].*)(?=.*[Dd]ebit.*)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)"
        return !containsIgnoredWords(message)
                && (Pattern.compile(regex).matcher(message).find()
                || DEBIT_PATTERN.toRegex().containsMatchIn(message.lowercase())
                || MISC_PATTERN.toRegex().containsMatchIn(message.lowercase()))
    }

    fun getAmountSpent(message: String): Double? {
        val regex = "(?i)(?:RS|INR|MRP)\\.?\\s?(\\d+(:?,\\d+)?(,\\d+)?(\\.\\d{1,2})?)"

        val matchGroup = Regex(regex).find(message)?.groups?.firstOrNull()
        return matchGroup?.value?.lowercase()
            ?.replace("inr.", "")?.replace("inr", "")
            ?.replace("mrp", "")
            ?.replace("rs.", "")?.replace("rs", "")
            ?.replace(",", "")?.trim()?.toDoubleOrNull()
    }

    fun isIncome(message: String): Boolean {
        val regex =
            "(?=.*[Aa]ccount.*|.*[Aa]/[Cc].*|.*[Aa][Cc][Cc][Tt].*|.*[Cc][Aa][Rr][Dd].*)(?=.*[Cc]redit.*)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)"
        return !containsIgnoredWords(message)
                && (Pattern.compile(regex).matcher(message).find()
                || CREDIT_PATTERN.toRegex().containsMatchIn(message.lowercase())
                || MISC_PATTERN.toRegex().containsMatchIn(message.lowercase()))
    }

    private fun containsIgnoredWords(message: String): Boolean {
        val lowercaseMessage = message.lowercase()
        return IGNORED_WORDS.any { lowercaseMessage.contains(it) }
    }
}

