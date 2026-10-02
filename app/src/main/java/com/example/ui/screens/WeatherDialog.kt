package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LauncherSettings
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun WeatherEditDialog(
    isOpen: Boolean,
    settings: LauncherSettings,
    isDark: Boolean,
    viewModel: LauncherViewModel,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val accent = settings.accent.color
    var city by remember { mutableStateOf(settings.weatherCity) }
    var temp by remember { mutableStateOf(settings.weatherTemp.toString()) }
    var condition by remember { mutableStateOf(settings.weatherCondition) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Виджет «Вкратце»: Погода",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0xFFE8EAED) else Color(0xFF202124)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Город") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = temp,
                    onValueChange = { temp = it },
                    label = { Text("Температура (°C)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = condition,
                    onValueChange = { condition = it },
                    label = { Text("Погода (Ясно, Облачно, Дождь)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedTemp = temp.toIntOrNull() ?: 20
                    viewModel.updateWeather(city, parsedTemp, condition)
                    onDismiss()
                }
            ) {
                Text("Сохранить", color = accent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        containerColor = if (isDark) Color(0xFF2D2E30) else Color(0xFFFFFFFF),
        shape = RoundedCornerShape(16.dp)
    )
}
