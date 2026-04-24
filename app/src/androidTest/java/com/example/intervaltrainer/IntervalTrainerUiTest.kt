package com.example.intervaltrainer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class IntervalTrainerUiTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun setupToTrainingFlow_works() {
        composeRule.onNodeWithText("Начать тренировку").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Тренировка").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("listen_again_button").assertIsDisplayed()
    }
}
