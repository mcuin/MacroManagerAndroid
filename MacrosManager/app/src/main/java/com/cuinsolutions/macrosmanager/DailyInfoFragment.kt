package com.cuinsolutions.macrosmanager

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.ListFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cuinsolutions.macrosmanager.databinding.FragmentDailyInfoBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

class DailyInfoFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentDailyInfoBinding
    private val macrosManagerViewModel: MacrosManagerViewModel by activityViewModels()
    private val viewModel: DailyInfoViewModel by viewModels()

    private var dailyCaloriesTotal = 0
    private var currentCaloriesTotal = 0.0
    private var dailyCarbsTotal = 0
    private var currentCarbsTotal = 0.0
    private var dailyFatTotal = 0
    private var currentFatTotal = 0.0
    private var dailyProteinTotal = 0
    private var currentProteinTotal = 0.0
    private var dailyMeals = listOf<HashMap<String, Any>>()
    private val gson = Gson()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_info, container, false)

        val settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
        fireStore.firestoreSettings = settings

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val alarmSet = (PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT) != null)

        if (alarmSet == false) {
            setResetAlarm()
        } else {
            setResetAlarm()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (macrosManagerViewModel.auth.currentUser != null) {
                    menuInflater.inflate(R.menu.action_bar_menu, menu)
                } else {
                    menuInflater.inflate(R.menu.no_user_menu, menu)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_sign_up -> findNavController().navigate(DailyInfoFragmentDirections.navigateToSignUp())
                    R.id.action_settings -> findNavController().navigate(DailyInfoFragmentDirections.navigateToSettings())
                    R.id.action_sign_in ->  findNavController().navigate(DailyInfoFragmentDirections.navigateToSignIn())
                }

                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.STARTED)


    }

    fun setResetAlarm() {
        val resetTime = Calendar.getInstance(Locale.getDefault())
        resetTime.timeInMillis = System.currentTimeMillis()
        resetTime.set(Calendar.HOUR_OF_DAY, 2)
        resetTime.set(Calendar.MINUTE, 0)
        resetTime.set(Calendar.SECOND, 0)

        val resetIntent = Intent(this, DailyResetAlarmReciever::class.java)
        val resetPendingIntent = PendingIntent.getBroadcast(this, 243, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val resetAlarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d("Reset", "set")

        resetAlarm.setInexactRepeating(AlarmManager.RTC, resetTime.timeInMillis, AlarmManager.INTERVAL_DAY, resetPendingIntent)
    }

    fun createUI() {

        if (dailyMeals.size != 0) {

            dailyMacrosGridView.adapter = DailyMacrosGridViewAdapter(this, macrosArrayList())
            userFoodRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
            userFoodRecyclerView.adapter = DailyMealsRecyclerViewAdapter(this, dailyMeals, DailyMacrosGridViewAdapter(this, macrosArrayList()))
        } else {


            userFoodRecyclerView.adapter = null
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.daily_add_meal_fab -> findNavController().navigate(DailyInfoFragmentDirections.navigateToAddMeal())
        }
    }
}