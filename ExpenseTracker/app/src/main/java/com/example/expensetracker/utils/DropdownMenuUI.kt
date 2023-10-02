package com.example.expensetracker.utils

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.expensetracker.data.models.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenuUI(list: List<String>, title: String) {
    var expanded by remember {
        mutableStateOf(false)
    }

    val sharedPreferences = LocalContext.current.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

    var selectedText by remember {
        mutableStateOf(sharedPreferences.getString("selected$title",list[0])!!)
    }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    OutlinedTextField(
        value = selectedText,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
            .onGloballyPositioned { coordinates ->
                textFieldSize = coordinates.size.toSize()
            },
        shape = RoundedCornerShape(12.dp),
        enabled = false,
        trailingIcon = {
            Icon(icon, contentDescription = title,
                Modifier.clickable { expanded=!expanded })
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            .background(
                MaterialTheme.colorScheme.surface
            )

    ) {
        list.forEach{label->
            DropdownMenuItem(
                onClick = {
                    with(sharedPreferences.edit()){
                        putString("selectedText",label).apply()
                    }
                    selectedText = label
                    expanded = false
                }
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

