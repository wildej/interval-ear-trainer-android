package com.example.intervaltrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.intervaltrainer.presentation.IntervalTrainerApp
import com.example.intervaltrainer.ui.theme.IntervaloTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntervaloTheme {
                Surface {
                    IntervalTrainerApp()
                }
            }
        }
    }
}
