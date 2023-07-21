package com.example.expensetracker.helper

sealed class Resource<out T : Any> {
    data class Success<out T : Any>(val data: T) : Resource<T>()
    object Loading : Resource<Nothing>()
    data class Error(val throwable: Throwable) : Resource<Nothing>()
}
