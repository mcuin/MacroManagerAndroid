package com.cuinsolutions.macrosmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cuinsolutions.macrosmanager.databinding.ActivityMacrosManagerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MacrosManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMacrosManagerBinding
    private lateinit var macrosManagerNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_macros_manager)

        binding.showAds = PreferencesManager(this).userInfo.showAds

        setSupportActionBar(binding.macrosManagerToolbar)
        macrosManagerNavController = (supportFragmentManager.findFragmentById(R.id.macros_manager_nav_container) as NavHostFragment).navController
        binding.macrosManagerBottomNav.setupWithNavController(macrosManagerNavController)

        macrosManagerNavController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.macros_calculator_fragment -> {
                    findNavController(binding.macrosManagerNavContainer.id).navigate(R.id.navigate_to_macros_calculator)
                }
            }
        }

        if (binding.showAds) {
            MobileAds.initialize(this) {
                val adRequest = AdRequest.Builder().build()
                binding.macrosManagerAdBanner.loadAd(adRequest)
            }
        }
    }
}