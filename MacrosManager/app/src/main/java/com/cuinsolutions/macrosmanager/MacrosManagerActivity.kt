package com.cuinsolutions.macrosmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.cuinsolutions.macrosmanager.ui.theme.MacrosManagerAndroidTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class MacrosManagerActivity : ComponentActivity() {

    private val macrosManagerViewModel: MacrosManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@MacrosManagerActivity) {}
        }

        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345", "AB57A6DD24A935804E0D882A06931552")).build()
        )
        setContent {
            MacrosManagerAndroidTheme {
                MacrosManagerApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            macrosManagerViewModel.macrosManagerPreferences.collect { preferences ->
                if (ChronoUnit.DAYS.between(preferences.currentDate, LocalDate.now()) >= 1) {
                    macrosManagerViewModel.deleteMeals()
                }
            }
        }
    }
}