package com.muxaeji.intervalo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.muxaeji.intervalo.presentation.IntervalTrainerApp
import com.muxaeji.intervalo.ui.theme.IntervaloTheme

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
