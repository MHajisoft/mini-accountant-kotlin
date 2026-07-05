package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.FinancialApp
import com.example.ui.FinanceViewModel
import com.example.ui.theme.FinancialAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val activeTheme by viewModel.theme.collectAsState()
            FinancialAppTheme(theme = activeTheme) {
                FinancialApp(viewModel = viewModel)
            }
        }
    }
}
