package com.example.namiokai.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.namiokai.ui.screens.NamiokaiApp
import com.example.namiokai.ui.theme.NamiokaiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            //setKeepOnScreenCondition()
        }
        setContent {
            NamiokaiTheme {
                NamiokaiApp()
            }
        }
    }
}


