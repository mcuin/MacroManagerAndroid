package com.cuinsolutions.macrosmanager

import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun SettingsScreen(modifier: Modifier, navController: NavHostController, settingsViewModel: SettingsViewModel = hiltViewModel()) {

    var showSaveFab by rememberSaveable { mutableStateOf(false) }

    Scaffold(modifier = modifier.navigationBarsPadding(), topBar = { MacrosManagerOptionsMenuAppBar(modifier = modifier, navController = navController, titleResourceId = R.string.settings) },
        floatingActionButton = { if (showSaveFab) SettingsSaveFAB(modifier = modifier, settingsViewModel = settingsViewModel, navController = navController) },
        bottomBar = { BannerAdview() }) {

        val states = settingsViewModel.states.collectAsStateWithLifecycle()

        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)
            .scrollable(enabled = true, state = rememberScrollState(), orientation = Orientation.Vertical)) {
            when (states.value) {
                is SettingsScreenUIState.Loading -> {}
                is SettingsScreenUIState.Success -> {
                    showSaveFab = settingsViewModel.userInfo.birthMonth > -1 && settingsViewModel.userInfo.birthYear > -1
                    SettingsBirthday(modifier = modifier, birthMonth = settingsViewModel.userInfo.birthMonth, birthYear = settingsViewModel.userInfo.birthYear, settingsViewModel = settingsViewModel)
                    SettingsGender(modifier = modifier, selectedGenderId = settingsViewModel.userInfo.gender, settingsViewModel = settingsViewModel)
                    SettingsHeightMeasurement(modifier = modifier, heightMeasurementId = settingsViewModel.userInfo.heightMeasurement, settingsViewModel = settingsViewModel)
                    SettingsWeightMeasurement(modifier = modifier, weightMeasurementId = settingsViewModel.userInfo.weightMeasurement, settingsViewModel = settingsViewModel)
                }
                is SettingsScreenUIState.Error -> {}
            }
        }


    }
}

@Composable
fun SettingsBirthday(modifier: Modifier, birthMonth: Int, birthYear: Int, settingsViewModel: SettingsViewModel) {

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var birthString by remember { mutableStateOf("") }
    birthString = if (birthMonth > 0 && birthYear > 0) {
            stringResource(id = R.string.birth_month_year_entry, birthMonth, birthYear)
        } else {
            stringResource(id = R.string.set_birth_date)
        }

    Text(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.margin_standard),
        end = dimensionResource(R.dimen.margin_standard),
        top = dimensionResource(R.dimen.margin_standard)), text = stringResource(id = R.string.birth_month_year))
    Text(text = birthString, modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.margin_standard),
        end = dimensionResource(R.dimen.margin_standard), top = dimensionResource(R.dimen.margin_small_minus)).clickable { showDatePicker = true })
    if (showDatePicker) {
        BirthPickerDialog(modifier, hideDatePicker = { showDatePicker = false },
            monthYearSelected = { month, year ->
                settingsViewModel.updateUserBirthInfo(month, year)
                showDatePicker = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthPickerDialog(modifier: Modifier, hideDatePicker: () -> Unit, monthYearSelected: (Int, Int) -> Unit) {

    val minCalendar = Calendar.getInstance()
    minCalendar.set(Calendar.YEAR, minCalendar.get(Calendar.YEAR) - 120)
    minCalendar.set(Calendar.MONTH, minCalendar.get(Calendar.MONTH))
    val maxCalendar = Calendar.getInstance()
    maxCalendar.set(Calendar.YEAR, maxCalendar.get(Calendar.YEAR) - 17)
    maxCalendar.set(Calendar.MONTH, maxCalendar.get(Calendar.MONTH))

    BasicAlertDialog (modifier = modifier, onDismissRequest = { }) {
        ComposeCalendar(minDate = minCalendar.time,
            maxDate = maxCalendar.time,
            calendarType = CalendarType.MONTH_AND_YEAR,
            title = "Select Birth Month and Year",
            listener = object : SelectDateListener {
                override fun onCanceled() {
                    hideDatePicker()
                }

                override fun onDateSelected(date: Date) {
                    val localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    monthYearSelected(localDate.monthValue, localDate.year)
                }
            })
    }
}

@Composable
fun SettingsGender(modifier: Modifier, selectedGenderId: Int, settingsViewModel: SettingsViewModel) {
    Text(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.margin_standard),
        end = dimensionResource(R.dimen.margin_standard), top = dimensionResource(R.dimen.margin_standard)),text = stringResource(id = R.string.gender))
    Gender.entries.forEach { gender ->
        MacrosManagerRadioButton(modifier = modifier.clickable { settingsViewModel.updateUserGender(gender.id) },
            title = gender.title,
            isSelected = selectedGenderId == gender.id)
    }
}

@Composable
fun SettingsHeightMeasurement(modifier: Modifier, heightMeasurementId: Int, settingsViewModel: SettingsViewModel) {
    Text(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.margin_standard),
        end = dimensionResource(R.dimen.margin_standard), top = dimensionResource(R.dimen.margin_standard)), text = stringResource(id = R.string.height_measurement))
    HeightMeasurement.entries.forEach { heightMeasurement ->
        MacrosManagerRadioButton(modifier = modifier.clickable { settingsViewModel.updateUserHeightMeasurement(heightMeasurement.id) },
            title = heightMeasurement.title,
            isSelected = heightMeasurementId == heightMeasurement.id)
    }
}

@Composable
fun SettingsWeightMeasurement(modifier: Modifier, weightMeasurementId: Int, settingsViewModel: SettingsViewModel) {
    Text(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.margin_standard),
        end = dimensionResource(R.dimen.margin_standard), top = dimensionResource(R.dimen.margin_standard)), text = stringResource(id = R.string.weight_measurement))
    WeightMeasurement.entries.forEach { weightMeasurement ->
        MacrosManagerRadioButton(modifier = modifier.clickable { settingsViewModel.updateUserWeightMeasurement(weightMeasurement.id) },
            title = weightMeasurement.title,
            isSelected = weightMeasurementId == weightMeasurement.id)
    }
}

@Composable
fun SettingsSaveFAB(modifier: Modifier, settingsViewModel: SettingsViewModel, navController: NavHostController) {
    FloatingActionButton(onClick = {
        settingsViewModel.saveSettings()
        navController.popBackStack()
    }) {
        Icon(painterResource(id = R.drawable.ic_save), contentDescription = null)
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(modifier = Modifier, navController = rememberNavController())
}