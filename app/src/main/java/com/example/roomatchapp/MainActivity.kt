package com.example.roomatchapp

import android.os.Bundle
import android.util.Log
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.roomatchapp.data.local.session.UserSessionManager
import com.example.roomatchapp.di.AppDependencies
import com.example.roomatchapp.di.CloudinaryModel
import com.example.roomatchapp.presentation.navigation.AppNavGraph
import com.example.roomatchapp.presentation.theme.Background
import com.example.roomatchapp.presentation.theme.RooMatchAppTheme
import com.example.roomatchapp.BuildConfig
import com.google.android.libraries.places.api.Places
import javax.inject.Inject


class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: UserSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        enableEdgeToEdge()
        // Initialize ROOM, Cloudinary and UserSessionManager
        sessionManager = UserSessionManager(applicationContext)
        AppDependencies.sessionManager = sessionManager
        AppDependencies.init(applicationContext)
        CloudinaryModel.init(this)

        setContent {
            RooMatchAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        AppNavGraph(sessionManager = sessionManager)
                    }
                }
            }
        }
    }
}

