package com.cuinsolutions.macrosmanager

import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cuinsolutions.macrosmanager.utils.DailyActivityLevel
import com.cuinsolutions.macrosmanager.utils.DietFatPercent
import com.cuinsolutions.macrosmanager.utils.Goal
import com.cuinsolutions.macrosmanager.utils.HeightMeasurement
import com.cuinsolutions.macrosmanager.utils.PhysicalActivityLifestyle
import com.cuinsolutions.macrosmanager.utils.WeightMeasurement

@Composable
fun MacrosCalculatorScreen(modifier: Modifier, navController: NavHostController, calculatorViewModel: MacrosCalculatorViewModel = hiltViewModel()) {

    var showCalculateFab by rememberSaveable { mutableStateOf(false) }

    Scaffold(modifier = modifier
        .navigationBarsPadding()
        .imePadding(), topBar = {
        MacrosManagerOptionsMenuAppBar(modifier = modifier, navController = navController, titleResourceId = R.string.calculator) },
        floatingActionButton = { if (showCalculateFab) CalculateFab(modifier = modifier, calculatorViewModel = calculatorViewModel, navController = navController) },
        bottomBar = { BottomNavigationBar(modifier = modifier, navController = navController) }) {

        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)
            .verticalScroll(enabled = true, state = rememberScrollState())) {

            val states = calculatorViewModel.states.collectAsStateWithLifecycle()

            when (states.value) {
                is CalculatorState.Loading -> {}
                is CalculatorState.Success -> {
                    showCalculateFab = calculatorViewModel.currentUserInfo.weightKg > 0.0 && calculatorViewModel.currentUserInfo.heightCm > 0.0
                    CalculatorHeight(modifier = modifier, heightMeasurement = calculatorViewModel.currentUserInfo.heightMeasurement, calculatorViewModel = calculatorViewModel, updateShowCalculateFab = { showCalculateFab = it })
                    CalculatorWeight(modifier = modifier, weightMeasurement = calculatorViewModel.currentUserInfo.weightMeasurement, calculatorViewModel = calculatorViewModel, updateShowCalculateFab = { showCalculateFab = it })
                    CalculatorDailyActivityLevel(modifier = modifier, dailyActivityLevelId = calculatorViewModel.currentCalculatorOptions.dailyActivity, calculatorViewModel = calculatorViewModel)
                    CalculatorPhysicalActivityLifestyle(modifier = modifier, physicalActivityLifestyleId = calculatorViewModel.currentCalculatorOptions.physicalActivityLifestyle, calculatorViewModel = calculatorViewModel)
                    CalculatorDietFatPercent(modifier = modifier, dietFatPercentId = calculatorViewModel.currentCalculatorOptions.dietFatPercentId, calculatorViewModel = calculatorViewModel,
                        updateShowCalculateFab = { showCalculateFab = it })
                    CalculatorGoal(modifier = modifier, goalId = calculatorViewModel.currentCalculatorOptions.goal, calculatorViewModel = calculatorViewModel)
                    BannerAdview()
                }
                is CalculatorState.SettingsError -> {
                    SettingsErrorAlertDialog(modifier = modifier, navController = navController)
                }
            }
        }
    }
}

@Composable
fun CalculatorHeight(modifier: Modifier, heightMeasurement: Int,
                     calculatorViewModel: MacrosCalculatorViewModel, updateShowCalculateFab: (Boolean) -> Unit) {

    val focusManager = LocalFocusManager.current
    var showRangeError by rememberSaveable { mutableStateOf(false) }
    var showEmptyError by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
            end = dimensionResource(R.dimen.margin_standard),
            top = dimensionResource(R.dimen.margin_standard)),
            text = stringResource(id = R.string.height))
        if (heightMeasurement == HeightMeasurement.METRIC.id) {
            TextField(modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_small_minus)
                ),
                value = calculatorViewModel.userCm,
                onValueChange = { cmText ->
                    val filteredCm = calculatorViewModel.validHeightCm(cmText)
                    if (filteredCm.isNotEmpty()) {
                        if (filteredCm.toDouble() !in 54.6..272.0) {
                            showRangeError = true
                        } else {
                            showRangeError = false
                        }
                        showEmptyError = false
                    } else {
                        showEmptyError = true
                    }
                    updateShowCalculateFab(!showEmptyError && !showRangeError)
                    calculatorViewModel.updateUserCm(filteredCm) },
                label = { Text(text = stringResource(id = R.string.centimeters)) },
                isError = showRangeError || showEmptyError,
                supportingText = {
                    when {
                        showEmptyError -> Text(text = stringResource(id = R.string.height_entry_error))
                        showRangeError -> Text(text = stringResource(id = R.string.height_entry_range_error))
                }},
                trailingIcon = { if (showRangeError || showEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
        } else {
            Row(modifier = modifier.fillMaxWidth()) {
                TextField(modifier = modifier
                    .weight(1f)
                    .padding(
                        start = dimensionResource(R.dimen.margin_standard),
                        end = dimensionResource(R.dimen.margin_small_minus),
                        top = dimensionResource(R.dimen.margin_small_minus)
                    ),
                    value = calculatorViewModel.userFeet,
                    onValueChange = { feetText ->
                        val filteredFeet = calculatorViewModel.validHeightFeet(feetText)
                        if (filteredFeet.isNotEmpty()) {
                            if (filteredFeet.toDouble() !in 1.0..8.0) {
                                showRangeError = true
                            } else {
                                showRangeError = false
                            }
                            showEmptyError = false
                        } else {
                            showEmptyError = true
                        }
                        updateShowCalculateFab(!showEmptyError && !showRangeError)
                        calculatorViewModel.updateUserFeet(filteredFeet) },
                    label = { Text(text = stringResource(id = R.string.feet)) },
                    isError = showRangeError || showEmptyError,
                    supportingText = {
                        when {
                            showEmptyError -> Text(text = stringResource(id = R.string.height_entry_error))
                            showRangeError -> Text(text = stringResource(id = R.string.height_entry_range_error))
                        }},
                    trailingIcon = { if (showRangeError || showEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
                TextField(modifier = modifier
                    .weight(1f)
                    .padding(
                        start = dimensionResource(R.dimen.margin_standard),
                        end = dimensionResource(R.dimen.margin_standard),
                        top = dimensionResource(R.dimen.margin_small_minus)
                    ),
                    value = calculatorViewModel.userInches,
                    label = { Text(text = stringResource(id = R.string.inches)) },
                    onValueChange = { inchesText ->
                        val filteredInches = calculatorViewModel.validHeightInches(inchesText)
                        if (filteredInches.isNotEmpty()) {
                            if (filteredInches.toDouble() !in 0.0..11.0) {
                                showRangeError = true
                            } else {
                                showRangeError = false
                            }
                            showEmptyError = false
                        } else {
                            showEmptyError = true
                        }
                        updateShowCalculateFab(!showEmptyError && !showRangeError)
                        calculatorViewModel.updateUserInches(inchesText) },
                    isError = showRangeError || showEmptyError,
                    supportingText = {
                        when {
                            showEmptyError -> Text(text = stringResource(id = R.string.height_entry_error))
                            showRangeError -> Text(text = stringResource(id = R.string.height_entry_range_error))
                        }},
                    trailingIcon = { if (showRangeError || showEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
            }
        }
    }
}

@Composable
fun CalculatorWeight(modifier: Modifier, weightMeasurement: Int,
                     calculatorViewModel: MacrosCalculatorViewModel, updateShowCalculateFab: (Boolean) -> Unit) {

    val focusManager = LocalFocusManager.current
    var showRangeError by rememberSaveable { mutableStateOf(false) }
    var showEmptyError by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
            end = dimensionResource(R.dimen.margin_standard),
            top = dimensionResource(R.dimen.margin_standard)),
            text = stringResource(id = R.string.weight))
        when (weightMeasurement) {
            WeightMeasurement.METRIC.id -> TextField(modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_small_minus)
                ),
                value = calculatorViewModel.userKg,
                onValueChange = { kgText ->
                    val filteredKg = calculatorViewModel.validWeightKg(kgText)
                    if (filteredKg.isNotEmpty()) {
                        if (filteredKg.toDouble() !in 2.0..635.0) {
                            showRangeError = true
                        } else {
                            showRangeError = false
                        }
                        showEmptyError = false
                    } else {
                        showEmptyError = true
                    }
                    updateShowCalculateFab(!showEmptyError && !showRangeError)
                    calculatorViewModel.updateUserKg(filteredKg) },
                label = { Text(text = stringResource(id = R.string.kilograms)) },
                isError = showRangeError || showEmptyError,
                supportingText = {
                    when {
                        showEmptyError -> Text(text = stringResource(id = R.string.weight_entry_error))
                        showRangeError -> Text(text = stringResource(id = R.string.weight_entry_range_error))
                    }},
                trailingIcon = { if (showRangeError || showEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
            WeightMeasurement.IMPERIAL.id -> TextField(modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_small_minus)
                ),
                value = calculatorViewModel.userPounds,
                onValueChange = { poundsText ->
                    val filteredPounds = calculatorViewModel.validWeightPounds(poundsText)
                    if (filteredPounds.isNotEmpty()) {
                        if (filteredPounds.toDouble() !in 4.7..1400.0) {
                            showRangeError = true
                        } else {
                            showRangeError = false
                        }
                        showEmptyError = false
                    } else {
                        showEmptyError = true
                    }
                    updateShowCalculateFab(!showEmptyError && !showRangeError)
                    calculatorViewModel.updateUserPounds(filteredPounds) },
                label = { Text(text = stringResource(id = R.string.pounds)) },
                isError = showRangeError || showEmptyError,
                supportingText = {
                    when {
                        showEmptyError -> Text(text = stringResource(id = R.string.weight_entry_error))
                        showRangeError -> Text(text = stringResource(id = R.string.weight_entry_range_error))
                    }},
                trailingIcon = { if (showRangeError || showEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
            WeightMeasurement.STONE.id -> {
                Row {
                    TextField(modifier = modifier
                        .weight(1f)
                        .padding(
                            start = dimensionResource(R.dimen.margin_standard),
                            end = dimensionResource(R.dimen.margin_small_minus),
                            top = dimensionResource(R.dimen.margin_small_minus)
                        ),
                        value = calculatorViewModel.userStone,
                        onValueChange = { stoneText ->
                            val filteredStone = calculatorViewModel.validWeightStone(stoneText)
                            if (filteredStone.isNotEmpty()) {
                                if (filteredStone.toDouble() !in 0.0..100.0) {
                                    showRangeError = true
                                } else {
                                    showRangeError = false
                                }
                                showEmptyError = false
                            } else {
                                showEmptyError = true
                            }
                            updateShowCalculateFab(!showEmptyError && !showRangeError)
                            calculatorViewModel.updateUserStone(filteredStone) },
                        label = { Text(text = stringResource(id = R.string.stone)) },
                        isError = showRangeError || showEmptyError,
                        supportingText = {
                            when {
                                showEmptyError -> Text(text = stringResource(id = R.string.weight_entry_error))
                                showRangeError -> Text(text = stringResource(id = R.string.weight_entry_range_error))
                            }},
                        trailingIcon = { if (showRangeError || showEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
                    TextField(modifier = modifier
                        .weight(1f)
                        .padding(
                            start = dimensionResource(R.dimen.margin_standard),
                            end = dimensionResource(R.dimen.margin_standard),
                            top = dimensionResource(R.dimen.margin_small_minus)
                        ),
                        value = calculatorViewModel.userPounds,
                        onValueChange = { poundsText ->
                            val filteredPounds = calculatorViewModel.validWeightPounds(poundsText)
                            if (filteredPounds.isNotEmpty()) {
                                if (filteredPounds.toDouble() !in 4.7..1400.0) {
                                    showRangeError = true
                                } else {
                                    showRangeError = false
                                }
                                showEmptyError = false
                            } else {
                                showEmptyError = true
                            }
                            updateShowCalculateFab(!showEmptyError && !showRangeError)
                            calculatorViewModel.updateUserPounds(filteredPounds) },
                        label = { Text(text = stringResource(id = R.string.pounds)) },
                        isError = showRangeError || showEmptyError,
                        supportingText = {
                            when {
                                showEmptyError -> Text(text = stringResource(id = R.string.weight_entry_error))
                                showRangeError -> Text(text = stringResource(id = R.string.weight_entry_range_error))
                                else -> Text(text = "")
                            }},
                        trailingIcon = { if (showRangeError || showEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
                }
            }
        }
    }
}


@Composable
fun CalculatorDailyActivityLevel(modifier: Modifier, dailyActivityLevelId: Int, calculatorViewModel: MacrosCalculatorViewModel) {

    var showDailyActivityLevelDialog by rememberSaveable { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
            top = dimensionResource(R.dimen.margin_standard)),
            text = stringResource(id = R.string.daily_activity_level))
        IconButton(modifier = modifier.padding(
            end = dimensionResource(R.dimen.margin_standard),
            top = dimensionResource(R.dimen.margin_small_minus)), onClick = { showDailyActivityLevelDialog = true }) {
            Icon(painter = painterResource(id = R.drawable.ic_info), contentDescription = null)
        }
    }
    DailyActivityLevel.entries.forEach { activityLevel ->
        MacrosManagerRadioButton(modifier = modifier.selectable(selected = activityLevel.id == dailyActivityLevelId,
            onClick = { calculatorViewModel.updateCalculatorOptionDailyActivity(activityLevel.id) },
            role = Role.RadioButton),
            title = activityLevel.title,
            isSelected = activityLevel.id == dailyActivityLevelId)
    }

    if (showDailyActivityLevelDialog) {
        MacrosCalculatorDailyActivityLevelDialog(modifier = modifier, onDismiss = { showDailyActivityLevelDialog = false })
    }
}

@Composable
fun CalculatorPhysicalActivityLifestyle(modifier: Modifier, physicalActivityLifestyleId: Int, calculatorViewModel: MacrosCalculatorViewModel) {
    Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
        end = dimensionResource(R.dimen.margin_standard),
        top = dimensionResource(R.dimen.margin_standard)),text = stringResource(id = R.string.physical_activity_lifestyle))
    PhysicalActivityLifestyle.entries.forEach { activityLevel ->
        MacrosManagerRadioButton(modifier = modifier.selectable(activityLevel.id == physicalActivityLifestyleId,
            onClick = { calculatorViewModel.updateCalculatorOptionPhysicalActivityLifestyle(activityLevel.id) },
            role = Role.RadioButton),
            title = activityLevel.title,
            isSelected = activityLevel.id == physicalActivityLifestyleId)
    }
}

@Composable
fun CalculatorDietFatPercent(modifier: Modifier, dietFatPercentId: Int, calculatorViewModel: MacrosCalculatorViewModel, updateShowCalculateFab: (Boolean) -> Unit) {

    var showDietFatPercentDialog by rememberSaveable { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
            end = dimensionResource(R.dimen.margin_standard),
            top = dimensionResource(R.dimen.margin_standard)),text = stringResource(id = R.string.diet_fat_percent))
        IconButton(modifier = modifier.padding(
            end = dimensionResource(R.dimen.margin_standard),
            top = dimensionResource(R.dimen.margin_small_minus)), onClick = { showDietFatPercentDialog = true }) {
            Icon(painter = painterResource(id = R.drawable.ic_info), contentDescription = null)
        }
    }
    DietFatPercent.entries.forEach { dietFatPercentOption ->
        MacrosManagerRadioButton(modifier = modifier.selectable(selected = dietFatPercentOption.id == dietFatPercentId,
            onClick = { calculatorViewModel.updateCalculatorOptionDietFatId(dietFatPercentOption.id, dietFatPercentOption.percent) },
            role = Role.RadioButton),
            title = dietFatPercentOption.title,
            isSelected = dietFatPercentOption.id == dietFatPercentId)
    }
    CalculatorCustomRadioButton(modifier = modifier.selectable(selected = dietFatPercentId == 3,
        onClick = { calculatorViewModel.updateCalculatorOptionDietFatId(3, 0.0) },
        role = Role.RadioButton),
        isSelected = dietFatPercentId == 3,
        calculatorViewModel = calculatorViewModel, updateShowCalculateFab = { updateShowCalculateFab(it) })

    if (showDietFatPercentDialog) {
        DietFatPercentDialog(modifier = modifier, onDismiss = { showDietFatPercentDialog = false })
    }
}

@Composable
fun CalculatorGoal(modifier: Modifier, goalId: Int, calculatorViewModel: MacrosCalculatorViewModel) {
    Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
        end = dimensionResource(R.dimen.margin_standard),
        top = dimensionResource(R.dimen.margin_standard)),text = stringResource(id = R.string.goal))
    Goal.entries.forEach { goal ->
        MacrosManagerRadioButton(modifier = modifier.selectable(selected = goal.id == goalId,
            onClick = { calculatorViewModel.updateCalculatorOptionGoal(goal.id) },
            role = Role.RadioButton),
            title = goal.title,
            isSelected = goal.id == goalId)
    }
}

@Composable
fun CalculatorCustomRadioButton(modifier: Modifier, isSelected: Boolean,
                                calculatorViewModel: MacrosCalculatorViewModel, updateShowCalculateFab: (Boolean) -> Unit) {

    val focusManager = LocalFocusManager.current
    var dietFatPercentEmptyError by rememberSaveable { mutableStateOf(false) }
    var dietFatPercentRangeError by rememberSaveable { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard)),
            selected = isSelected, onClick = null)
        TextField(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_small_minus)),
            value = calculatorViewModel.userDietFatPercent,
            onValueChange = { dietFatPercentText ->
                val dietFatPercentFiltered = calculatorViewModel.validDietFatPercent(dietFatPercentText)
                when {
                    dietFatPercentFiltered !in ("25".."35") -> dietFatPercentRangeError = true
                    dietFatPercentFiltered.isEmpty() -> dietFatPercentEmptyError = true
                    else -> {
                        dietFatPercentEmptyError = false
                        dietFatPercentRangeError = false
                    }
                }
                updateShowCalculateFab(!dietFatPercentEmptyError && !dietFatPercentRangeError)
                calculatorViewModel.updateDietFatPercent(dietFatPercentFiltered) },
            isError = dietFatPercentEmptyError || dietFatPercentRangeError,
            supportingText = {
                when {
                    dietFatPercentEmptyError -> Text(text = stringResource(id = R.string.custom_fat_entry_error))
                    dietFatPercentRangeError -> Text(text = stringResource(id = R.string.custom_fat_entry_range_error))
                }
            },
            label = { Text(text = stringResource(id = R.string.custom)) },
            enabled = isSelected,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )
    }
}

@Composable
fun CalculateFab(modifier: Modifier, calculatorViewModel: MacrosCalculatorViewModel, navController: NavHostController) {
    ExtendedFloatingActionButton(modifier = modifier, onClick = {
        calculatorViewModel.calculate()
        navController.popBackStack()
    }) {
        Text(text = stringResource(id = R.string.calc))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacrosCalculatorDailyActivityLevelDialog(modifier: Modifier, onDismiss: () -> Unit) {
    BasicAlertDialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = modifier.fillMaxWidth()) {
            Text(modifier = modifier
                .padding(
                    start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_small_minus)
                )
                .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.daily_activity_level))
            Text(modifier = modifier
                .padding(
                    start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_small_minus)
                )
                .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.daily_activity_level_explanation))
            TextButton(modifier = modifier
                .align(Alignment.End)
                .padding(
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_standard),
                    bottom = dimensionResource(R.dimen.margin_standard)
                ), onClick = { onDismiss() }) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietFatPercentDialog(modifier: Modifier, onDismiss: () -> Unit) {
    BasicAlertDialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = modifier.fillMaxWidth()) {
            Text(modifier = modifier
                .padding(
                    start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_small_minus)
                )
                .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.diet_fat_percent))
            Text(modifier = modifier
                .padding(
                    start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_small_minus)
                )
                .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.fat_percent_explanation))
            TextButton(modifier = modifier
                .align(Alignment.End)
                .padding(
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_standard),
                    bottom = dimensionResource(R.dimen.margin_standard)
                ), onClick = { onDismiss() }) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsErrorAlertDialog(modifier: Modifier, navController: NavHostController) {
        BasicAlertDialog(modifier = modifier, onDismissRequest = { navController.navigateUp() }) {
            Card {
                Column(modifier = modifier.fillMaxWidth()) {
                    Text(modifier = modifier.align(Alignment.CenterHorizontally), text = stringResource(id = R.string.settings_error))
                    Text(modifier = modifier.align(Alignment.CenterHorizontally), text = stringResource(id = R.string.settings_error_description))
                    Row(modifier = modifier.align(Alignment.End)) {
                        TextButton(onClick = {
                            navController.navigate(Screens.Settings.route)
                        }) {
                            Text(text = stringResource(id = R.string.go_to_settings))
                        }
                        TextButton(onClick = { navController.navigateUp() }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                }
            }
        }
}

@Preview(showBackground = true)
@Composable
fun MacrosCalculatorScreenPreview() {
    MacrosCalculatorScreen(modifier = Modifier, navController = rememberNavController())
}