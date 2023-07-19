package com.example.expensetracker.data.models

import com.example.expensetracker.R

data class Category(
    val name: String,
    val iconResId: Int
)
object TransactionCategories {
    const val FOOD = "Food"
    const val TRAVEL = "Travel"
    const val HOSPITAL = "Hospital"
    const val EDUCATION = "Education"
    const val BILLS = "Bills"
    const val GROCERY = "Grocery"
    const val CLOTHES = "Clothes"
    const val MOVIES = "Movies"
    const val OTHERS = "Others"

    val categories = listOf(
        Category(FOOD, R.drawable.ic_food),
        Category(TRAVEL, R.drawable.ic_travel),
        Category(HOSPITAL, R.drawable.ic_hospital),
        Category(EDUCATION, R.drawable.ic_education),
        Category(BILLS, R.drawable.ic_bills),
        Category(GROCERY, R.drawable.ic_grocery),
        Category(CLOTHES, R.drawable.ic_clothes),
        Category(MOVIES, R.drawable.ic_movies),
        Category(OTHERS, R.drawable.ic_others)
    )
}
