package com.divine.traveller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.divine.traveller.navigation.TravellerNavigation
import com.divine.traveller.ui.theme.TravellerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TravellerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TravellerNavigation()
                }
            }
        }
    }
}