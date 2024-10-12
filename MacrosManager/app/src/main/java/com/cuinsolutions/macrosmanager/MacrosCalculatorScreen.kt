package com.cuinsolutions.macrosmanager

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MacrosCalculatorScreen(modifier: Modifier, navController: NavHostController, calculatorViewModel: MacrosCalculatorViewModel = hiltViewModel()) {

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(modifier = modifier
        .navigationBarsPadding()
        .imePadding(), topBar = {
        MacrosManagerOptionsMenuAppBar(modifier = modifier, navController = navController, titleResourceId = R.string.calculator) },
        floatingActionButton = { CalculateFab(modifier = modifier, calculatorViewModel = calculatorViewModel, navController = navController, snackbarHostState = snackbarHostState, scope = scope) },
        bottomBar = { BottomNavigationBar(modifier = modifier, navController = navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {

        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)
            .verticalScroll(enabled = true, state = rememberScrollState())) {

            val states = calculatorViewModel.states.collectAsStateWithLifecycle()

            when (states.value) {
                is CalculatorState.Loading -> {}
                is CalculatorState.Success -> {
                    CalculatorHeight(modifier = modifier, heightMeasurement = calculatorViewModel.currentUserInfo.heightMeasurement, calculatorViewModel = calculatorViewModel)
                    CalculatorWeight(modifier = modifier, weightMeasurement = calculatorViewModel.currentUserInfo.weightMeasurement, calculatorViewModel = calculatorViewModel)
                    CalculatorDailyActivityLevel(modifier = modifier, dailyActivityLevelId = calculatorViewModel.currentCalculatorOptions.dailyActivity, calculatorViewModel = calculatorViewModel)
                    CalculatorPhysicalActivityLifestyle(modifier = modifier, physicalActivityLifestyleId = calculatorViewModel.currentCalculatorOptions.physicalActivityLifestyle, calculatorViewModel = calculatorViewModel)
                    CalculatorDietFatPercent(modifier = modifier, dietFatPercentId = calculatorViewModel.currentCalculatorOptions.dietFatPercentId, calculatorViewModel = calculatorViewModel)
                    CalculatorGoal(modifier = modifier, goalId = calculatorViewModel.currentCalculatorOptions.goal, calculatorViewModel = calculatorViewModel)
                    BannerAdview(stringResource(id = R.string.calculator_ad_unit_id))
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
                     calculatorViewModel: MacrosCalculatorViewModel) {

    val focusManager = LocalFocusManager.current

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
                    calculatorViewModel.updateUserCm(cmText) },
                label = { Text(text = stringResource(id = R.string.centimeters)) },
                isError = calculatorViewModel.userCmRangeError || calculatorViewModel.userCmEmptyError,
                supportingText = {
                    when {
                        calculatorViewModel.userCmEmptyError -> Text(text = stringResource(id = R.string.height_entry_error))
                        calculatorViewModel.userCmRangeError -> Text(text = stringResource(id = R.string.height_entry_range_error))
                }},
                trailingIcon = { if (calculatorViewModel.userCmRangeError || calculatorViewModel.userCmEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
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
                        calculatorViewModel.updateUserFeet(feetText) },
                    label = { Text(text = stringResource(id = R.string.feet)) },
                    isError = calculatorViewModel.userFeetRangeError || calculatorViewModel.userFeetEmptyError,
                    supportingText = {
                        when {
                            calculatorViewModel.userFeetEmptyError -> Text(text = stringResource(id = R.string.height_entry_error))
                            calculatorViewModel.userFeetRangeError -> Text(text = stringResource(id = R.string.height_entry_range_error))
                        }},
                    trailingIcon = { if (calculatorViewModel.userFeetRangeError || calculatorViewModel.userFeetEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
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
                        calculatorViewModel.updateUserInches(inchesText) },
                    isError = calculatorViewModel.userInchesRangeError || calculatorViewModel.userInchesEmptyError,
                    supportingText = {
                        when {
                            calculatorViewModel.userInchesEmptyError -> Text(text = stringResource(id = R.string.height_entry_error))
                            calculatorViewModel.userInchesRangeError -> Text(text = stringResource(id = R.string.height_entry_range_error))
                        }},
                    trailingIcon = { if (calculatorViewModel.userInchesRangeError || calculatorViewModel.userInchesEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
            }
        }
    }
}

@Composable
fun CalculatorWeight(modifier: Modifier, weightMeasurement: Int,
                     calculatorViewModel: MacrosCalculatorViewModel) {

    val focusManager = LocalFocusManager.current

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
                    calculatorViewModel.updateUserKg(kgText) },
                label = { Text(text = stringResource(id = R.string.kilograms)) },
                isError = calculatorViewModel.userKgRangeError || calculatorViewModel.userKgEmptyError,
                supportingText = {
                    when {
                        calculatorViewModel.userKgEmptyError -> Text(text = stringResource(id = R.string.weight_entry_error))
                        calculatorViewModel.userKgRangeError -> Text(text = stringResource(id = R.string.weight_entry_range_error))
                    }},
                trailingIcon = { if (calculatorViewModel.userKgRangeError || calculatorViewModel.userKgEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
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
                    calculatorViewModel.updateUserPounds(poundsText) },
                label = { Text(text = stringResource(id = R.string.pounds)) },
                isError = calculatorViewModel.userPoundsRangeError || calculatorViewModel.userPoundsEmptyError,
                supportingText = {
                    when {
                        calculatorViewModel.userPoundsEmptyError -> Text(text = stringResource(id = R.string.weight_entry_error))
                        calculatorViewModel.userPoundsRangeError -> Text(text = stringResource(id = R.string.weight_entry_range_error))
                    }},
                trailingIcon = { if (calculatorViewModel.userPoundsRangeError || calculatorViewModel.userPoundsEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
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
                            calculatorViewModel.updateUserStone(stoneText) },
                        label = { Text(text = stringResource(id = R.string.stone)) },
                        isError = calculatorViewModel.userStoneRangeError || calculatorViewModel.userStoneEmptyError,
                        supportingText = {
                            when {
                                calculatorViewModel.userStoneEmptyError -> Text(text = stringResource(id = R.string.weight_entry_error))
                                calculatorViewModel.userStoneRangeError -> Text(text = stringResource(id = R.string.weight_entry_range_error))
                            }},
                        trailingIcon = { if (calculatorViewModel.userStoneRangeError || calculatorViewModel.userStoneEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
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
                            calculatorViewModel.updateUserPoundsStone(poundsText) },
                        label = { Text(text = stringResource(id = R.string.pounds)) },
                        isError = calculatorViewModel.userPoundsRangeError || calculatorViewModel.userPoundsEmptyError,
                        supportingText = {
                            when {
                                calculatorViewModel.userPoundsEmptyError -> Text(text = stringResource(id = R.string.weight_entry_error))
                                calculatorViewModel.userPoundsRangeError -> Text(text = stringResource(id = R.string.weight_entry_range_error))
                                else -> Text(text = "")
                            }},
                        trailingIcon = { if (calculatorViewModel.userPoundsRangeError || calculatorViewModel.userPoundsEmptyError) Icon(painter = painterResource(id = R.drawable.ic_error), contentDescription = null) },
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
fun CalculatorDietFatPercent(modifier: Modifier, dietFatPercentId: Int, calculatorViewModel: MacrosCalculatorViewModel) {

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
        calculatorViewModel = calculatorViewModel)

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
                                calculatorViewModel: MacrosCalculatorViewModel) {

    val focusManager = LocalFocusManager.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard)),
            selected = isSelected, onClick = null)
        TextField(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_small_minus)),
            value = calculatorViewModel.userDietFatPercent,
            onValueChange = { dietFatPercentText ->
                calculatorViewModel.updateDietFatPercent(dietFatPercentText) },
            isError = calculatorViewModel.userDietFatPercentEmptyError || calculatorViewModel.userDietFatPercentRangeError,
            supportingText = {
                when {
                    calculatorViewModel.userDietFatPercentEmptyError -> Text(text = stringResource(id = R.string.custom_fat_entry_error))
                    calculatorViewModel.userDietFatPercentRangeError -> Text(text = stringResource(id = R.string.custom_fat_entry_range_error))
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
fun CalculateFab(modifier: Modifier, calculatorViewModel: MacrosCalculatorViewModel, navController: NavHostController, snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
    ExtendedFloatingActionButton(modifier = modifier.padding(bottom = dimensionResource(R.dimen.banner_ad_padding)), onClick = {
        val heightEntryError = when (calculatorViewModel.currentUserInfo.heightMeasurement) {
            HeightMeasurement.METRIC.id -> calculatorViewModel.userCmEmptyError || calculatorViewModel.userCmRangeError || calculatorViewModel.userCm.isEmpty()
            HeightMeasurement.IMPERIAL.id -> calculatorViewModel.userFeetEmptyError || calculatorViewModel.userFeetRangeError || calculatorViewModel.userInchesEmptyError || calculatorViewModel.userInchesRangeError ||
                    calculatorViewModel.userFeet.isEmpty() || calculatorViewModel.userInches.isEmpty()
            else -> false
        }
        val weightEntryError = when (calculatorViewModel.currentUserInfo.weightMeasurement) {
            WeightMeasurement.METRIC.id -> calculatorViewModel.userKgEmptyError || calculatorViewModel.userKgRangeError || calculatorViewModel.userKg.isEmpty()
            WeightMeasurement.IMPERIAL.id -> calculatorViewModel.userPoundsEmptyError || calculatorViewModel.userPoundsRangeError || calculatorViewModel.userPounds.isEmpty()
            WeightMeasurement.STONE.id -> calculatorViewModel.userStoneEmptyError || calculatorViewModel.userStoneRangeError || calculatorViewModel.userPoundsEmptyError || calculatorViewModel.userPoundsRangeError ||
                    calculatorViewModel.userStone.isEmpty() || calculatorViewModel.userPounds.isEmpty()
            else -> false
        }
        val dietFatPercentError = if (calculatorViewModel.currentCalculatorOptions.dietFatPercentId == 3) {
            calculatorViewModel.userDietFatPercentEmptyError || calculatorViewModel.userDietFatPercentRangeError || calculatorViewModel.userDietFatPercent.isEmpty()
        } else {
            false
        }
        if (heightEntryError || weightEntryError || dietFatPercentError) {
            scope.launch {
                snackbarHostState.showSnackbar(message = "Fill out or fix all errors before calculating")
            }
        } else {
            calculatorViewModel.calculate()
            navController.popBackStack()
        }
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
                    Text(modifier = modifier.padding(top = dimensionResource(R.dimen.margin_small), start = dimensionResource(R.dimen.margin_standard), end = dimensionResource(R.dimen.margin_standard)).align(Alignment.CenterHorizontally), text = stringResource(id = R.string.settings_error))
                    Text(modifier = modifier.padding(top = dimensionResource(R.dimen.margin_small), start = dimensionResource(R.dimen.margin_standard), end = dimensionResource(R.dimen.margin_standard)).align(Alignment.CenterHorizontally), text = stringResource(id = R.string.settings_error_description))
                    Row(modifier = modifier.padding(top = dimensionResource(R.dimen.margin_small), end = dimensionResource(R.dimen.margin_xsmall)).align(Alignment.End)) {
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