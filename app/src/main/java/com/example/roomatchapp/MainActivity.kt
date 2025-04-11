package com.example.roomatchapp

import android.os.Bundle
import androidx.compose.ui.unit.LayoutDirection
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.roomatchapp.presentation.navigation.AppNavGraph
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RooMatchAppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = Background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr){
                        AppNavGraph()
                    }
                }
            }
        }
    }
}

