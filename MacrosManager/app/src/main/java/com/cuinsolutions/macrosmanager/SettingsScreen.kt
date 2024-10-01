package com.cuinsolutions.macrosmanager

import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cuinsolutions.macrosmanager.utils.Gender
import com.cuinsolutions.macrosmanager.utils.HeightMeasurement
import com.cuinsolutions.macrosmanager.utils.WeightMeasurement
import com.dt.composedatepicker.CalendarType
import com.dt.composedatepicker.ComposeCalendar
import com.dt.composedatepicker.SelectDateListener
import java.util.Date

@Composable
fun SettingsScreen(modifier: Modifier, navController: NavHostController) {
    Scaffold(modifier = modifier.navigationBarsPadding(), topBar = { MacrosManagerOptionsMenuAppBar(modifier = modifier, navController = navController, titleResourceId = R.string.settings) },
        floatingActionButton = { SettingsSaveFAB(modifier = modifier) },
        bottomBar = { BannerAdview() }) {

        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)
            .scrollable(enabled = true, state = rememberScrollState(), orientation = Orientation.Vertical)) {
            SettingsBirthday(modifier = modifier)
            SettingsGender(modifier = modifier)
            SettingsHeightMeasurement(modifier = modifier)
            SettingsWeightMeasurement(modifier = modifier)
        }
    }
}

@Composable
fun SettingsBirthday(modifier: Modifier) {

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var birthMonth by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    val birthString by remember {
        if (birthMonth.isNotEmpty() && birthYear.isNotEmpty()) {
            mutableStateOf("$birthMonth/$birthYear")
        } else {
            mutableStateOf("Birth Month and Year")
        }
    }

    Text(text = birthString)
    Text(text = birthString, modifier.clickable { showDatePicker = true })
    if (showDatePicker) {
        BirthPickerDialog(modifier, hideDatePicker = { showDatePicker = false },
            monthYearSelected = { month, year ->
                birthMonth = month.toString()
                birthYear = year.toString()
                showDatePicker = false
        })
    }
}

@Composable
fun BirthPickerDialog(modifier: Modifier, hideDatePicker: () -> Unit, monthYearSelected: (Int, Int) -> Unit) {

    val minCalendar = Calendar.getInstance()
    minCalendar.set(Calendar.YEAR, minCalendar.get(Calendar.YEAR) - 120)
    minCalendar.set(Calendar.MONTH, minCalendar.get(Calendar.MONTH))
    val maxCalendar = Calendar.getInstance()
    maxCalendar.set(Calendar.YEAR, maxCalendar.get(Calendar.YEAR) - 17)
    maxCalendar.set(Calendar.MONTH, maxCalendar.get(Calendar.MONTH))

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ComposeCalendar(minDate = minCalendar.time,
            maxDate = maxCalendar.time,
            calendarType = CalendarType.MONTH_AND_YEAR,
            title = "Select Birth Month and Year",
            listener = object : SelectDateListener {
                override fun onCanceled() {
                    hideDatePicker()
                }

                override fun onDateSelected(date: Date) {
                    monthYearSelected(date.month, date.year)
                }
            })
    }
}

@Composable
fun SettingsGender(modifier: Modifier) {
    Text(text = stringResource(id = R.string.gender))
    Gender.entries.forEach { gender ->
        MacrosManagerRadioButton(modifier = modifier.clickable { /*TODO*/ }, title = gender.title)
    }
}

@Composable
fun SettingsHeightMeasurement(modifier: Modifier) {
    Text(text = stringResource(id = R.string.height_measurement))
    HeightMeasurement.entries.forEach { heightMeasurement ->
        MacrosManagerRadioButton(modifier = modifier.clickable { /*TODO*/ }, title = heightMeasurement.title)
    }
}

@Composable
fun SettingsWeightMeasurement(modifier: Modifier) {
    Text(text = stringResource(id = R.string.weight_measurement))
    WeightMeasurement.entries.forEach { weightMeasurement ->
        MacrosManagerRadioButton(modifier = modifier.clickable { /*TODO*/ }, title = weightMeasurement.title)
    }
}

@Composable
fun SettingsSaveFAB(modifier: Modifier) {
    FloatingActionButton(onClick = { /*TODO*/ }) {
        Icon(painterResource(id = R.drawable.ic_save), contentDescription = null)

    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(modifier = Modifier, navController = rememberNavController())
}