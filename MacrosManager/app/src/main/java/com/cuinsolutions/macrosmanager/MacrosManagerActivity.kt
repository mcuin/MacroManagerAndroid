package com.cuinsolutions.macrosmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.room.Insert
import com.cuinsolutions.macrosmanager.ui.theme.MacrosManagerAndroidTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.common.util.Clock
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.joda.time.DateTimeField
import org.joda.time.Instant
import org.joda.time.LocalDateTime
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MacrosManagerActivity : ComponentActivity() {

    private val macrosManagerViewModel: MacrosManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
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