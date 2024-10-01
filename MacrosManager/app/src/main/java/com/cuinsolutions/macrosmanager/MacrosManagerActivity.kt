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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cuinsolutions.macrosmanager.databinding.ActivityMacrosManagerBinding
import com.cuinsolutions.macrosmanager.ui.theme.MacrosManagerAndroidTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MacrosManagerActivity : ComponentActivity() {

    private lateinit var binding: ActivityMacrosManagerBinding
    private lateinit var macrosManagerNavController: NavController
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

        /*binding = DataBindingUtil.setContentView(this, R.layout.activity_macros_manager)

        binding.showAds = macrosManagerViewModel.currentUserInfo.value.showAds

        setSupportActionBar(binding.macrosManagerToolbar)
        macrosManagerNavController = (supportFragmentManager.findFragmentById(R.id.macros_manager_nav_container) as NavHostFragment).navController
        binding.macrosManagerBottomNav.setupWithNavController(macrosManagerNavController)

        macrosManagerNavController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.macros_calculator_fragment -> {
                    binding.hideBottomNav = false
                }
                R.id.daily_info_fragment -> {
                    binding.hideBottomNav = false
                }
                else -> {
                    binding.hideBottomNav = true
                }
            }
        }

        if (binding.showAds) {
            MobileAds.initialize(this) {
                val adRequest = AdRequest.Builder().build()
                binding.macrosManagerAdBanner.loadAd(adRequest)
            }
        }

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val alarmSet = (PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT) != null)

        if (alarmSet == false) {
            setResetAlarm()
        } else {
            setResetAlarm()
        }
    }

    fun setResetAlarm() {
        val resetTime = Calendar.getInstance(Locale.getDefault())
        resetTime.timeInMillis = System.currentTimeMillis()
        resetTime.set(Calendar.HOUR_OF_DAY, 2)
        resetTime.set(Calendar.MINUTE, 0)
        resetTime.set(Calendar.SECOND, 0)

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val resetPendingIntent = PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val resetAlarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d("Reset", "set")

        resetAlarm.setInexactRepeating(AlarmManager.RTC, resetTime.timeInMillis, AlarmManager.INTERVAL_DAY, resetPendingIntent)
    }*/
}