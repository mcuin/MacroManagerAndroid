package com.cuinsolutions.macrosmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
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
    Scaffold(modifier = modifier.navigationBarsPadding(), topBar = {
        MacrosManagerOptionsMenuAppBar(modifier = modifier, navController = navController, titleResourceId = R.string.calculator) },
        floatingActionButton = { CalculateFab(modifier = modifier) },
        bottomBar = { BottomNavigationBar(modifier = modifier, navController = navController) }) {

        Column(modifier = modifier
            .fillMaxSize()
            .padding(it).verticalScroll(enabled = true, state = rememberScrollState())) {
            CalculatorHeight(modifier = modifier, calculatorViewModel = calculatorViewModel)
            CalculatorWeight(modifier = modifier, calculatorViewModel = calculatorViewModel)
            CalculatorDailyActivityLevel(modifier = modifier, dailyActivityLevel = calculatorViewModel.currentCalculatorOptions.dailyActivity, calculatorViewModel = calculatorViewModel)
            CalculatorPhysicalActivityLifestyle(modifier = modifier)
            CalculatorDietFatPercent(modifier = modifier)
            CalculatorGoal(modifier = modifier)
            BannerAdview()
        }
    }
}

@Composable
fun CalculatorHeight(modifier: Modifier, heightMeasurement: HeightMeasurement = HeightMeasurement.METRIC,
                     calculatorViewModel: MacrosCalculatorViewModel) {

    Column(modifier = modifier.fillMaxWidth()) {
        Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
            end = dimensionResource(R.dimen.margin_standard),
            top = dimensionResource(R.dimen.margin_standard)),
            text = stringResource(id = R.string.height))
        if (heightMeasurement == HeightMeasurement.METRIC) {
            TextField(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.margin_standard),
                end = dimensionResource(R.dimen.margin_standard),
                top = dimensionResource(R.dimen.margin_small_minus)),
                value = calculatorViewModel.userCm,
                onValueChange = { cmText -> calculatorViewModel.updateUserCm(cmText) },
                label = { Text(text = stringResource(id = R.string.centimeters)) })
        } else {
            Row(modifier = modifier.fillMaxWidth()) {
                TextField(modifier = modifier.weight(1f).padding(start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_small_minus),
                    top = dimensionResource(R.dimen.margin_small_minus)),
                    value = calculatorViewModel.userFeet,
                    onValueChange = { feetText -> calculatorViewModel.updateUserFeet(feetText) },
                    label = { Text(text = stringResource(id = R.string.feet)) })
                TextField(modifier = modifier.weight(1f).padding(start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_small_minus)),
                    value = calculatorViewModel.userInches,
                    onValueChange = { inchesText -> calculatorViewModel.updateUserInches(inchesText) },
                    label = { Text(text = stringResource(id = R.string.inches)) })
            }
        }
    }
}

@Composable
fun CalculatorWeight(modifier: Modifier, weightMeasurement: WeightMeasurement = WeightMeasurement.METRIC,
                     calculatorViewModel: MacrosCalculatorViewModel) {

    Column(modifier = modifier.fillMaxWidth()) {
        Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
            end = dimensionResource(R.dimen.margin_standard),
            top = dimensionResource(R.dimen.margin_standard)),
            text = stringResource(id = R.string.weight))
        when (weightMeasurement) {
            WeightMeasurement.METRIC -> TextField(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.margin_standard),
                    end = dimensionResource(R.dimen.margin_standard),
                    top = dimensionResource(R.dimen.margin_small_minus)),
                value = calculatorViewModel.userKg,
                onValueChange = { kgText -> calculatorViewModel.updateUserKg(kgText) },
                label = { Text(text = stringResource(id = R.string.kilograms)) })
            WeightMeasurement.IMPERIAL -> TextField(modifier = modifier.fillMaxWidth().padding(start = dimensionResource(R.dimen.margin_standard),
                end = dimensionResource(R.dimen.margin_standard),
                top = dimensionResource(R.dimen.margin_small_minus)),
                value = calculatorViewModel.userPounds,
                onValueChange = { poundsText -> calculatorViewModel.updateUserPounds(poundsText) },
                label = { Text(text = stringResource(id = R.string.pounds)) })
            WeightMeasurement.STONE -> {
                Row {
                    TextField(modifier = modifier.weight(1f).padding(start = dimensionResource(R.dimen.margin_standard),
                        end = dimensionResource(R.dimen.margin_small_minus),
                        top = dimensionResource(R.dimen.margin_small_minus)),
                        value = calculatorViewModel.userStone,
                        onValueChange = { stoneText -> calculatorViewModel.updateUserStone(stoneText) },
                        label = { Text(text = stringResource(id = R.string.stone)) })
                    TextField(modifier = modifier.weight(1f).padding(start = dimensionResource(R.dimen.margin_standard),
                        end = dimensionResource(R.dimen.margin_standard),
                        top = dimensionResource(R.dimen.margin_small_minus)),
                        value = calculatorViewModel.userPounds,
                        onValueChange = { poundsText -> calculatorViewModel.updateUserPounds(poundsText) },
                        label = { Text(text = stringResource(id = R.string.pounds)) })
                }
            }
        }
    }
}


@Composable
fun CalculatorDailyActivityLevel(modifier: Modifier, dailyActivityLevel: Int, calculatorViewModel: MacrosCalculatorViewModel) {

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
        MacrosManagerRadioButton(modifier = modifier.selectable(selected = activityLevel.id == dailyActivityLevel, onClick = { calculatorViewModel.currentCalculatorOptions.dailyActivity = activityLevel.id }, role = Role.RadioButton), title = activityLevel.title)
    }

    if (showDailyActivityLevelDialog) {
        MacrosCalculatorDailyActivityLevelDialog(modifier = modifier, onDismiss = { showDailyActivityLevelDialog = false })
    }
}

@Composable
fun CalculatorPhysicalActivityLifestyle(modifier: Modifier) {
    Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
        end = dimensionResource(R.dimen.margin_standard),
        top = dimensionResource(R.dimen.margin_standard)),text = stringResource(id = R.string.physical_activity_lifestyle))
    PhysicalActivityLifestyle.entries.forEach { activityLevel ->
        MacrosManagerRadioButton(modifier = modifier.selectable(selected = false, onClick = { /*TODO*/ }, role = Role.RadioButton), title = activityLevel.title)
    }
}

@Composable
fun CalculatorDietFatPercent(modifier: Modifier) {

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
    DietFatPercent.entries.forEach { dietFatPercent ->
        MacrosManagerRadioButton(modifier = modifier.selectable(selected = false, onClick = { /*TODO*/ }, role = Role.RadioButton), title = dietFatPercent.title)
    }
    CalculatorCustomRadioButton(modifier = modifier.selectable(selected = false, onClick = { /*TODO*/ }, role = Role.RadioButton))

    if (showDietFatPercentDialog) {
        DietFatPercentDialog(modifier = modifier, onDismiss = { showDietFatPercentDialog = false })
    }
}

@Composable
fun CalculatorGoal(modifier: Modifier) {
    Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
        end = dimensionResource(R.dimen.margin_standard),
        top = dimensionResource(R.dimen.margin_standard)),text = stringResource(id = R.string.goal))
    Goal.entries.forEach { goal ->
        MacrosManagerRadioButton(modifier = modifier.selectable(selected = false, onClick = { /*TODO*/ }, role = Role.RadioButton), title = goal.title)
    }
}

@Composable
fun CalculatorCustomRadioButton(modifier: Modifier) {
    Row {
        RadioButton(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard)), selected = false, onClick = null)
        TextField(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_small_minus)), value = "", onValueChange = {}, label = { Text(text = stringResource(id = R.string.custom)) })
    }
}

@Composable
fun CalculateFab(modifier: Modifier) {
    ExtendedFloatingActionButton(onClick = { /*TODO*/ }) {
        Text(text = stringResource(id = R.string.calc))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacrosCalculatorDailyActivityLevelDialog(modifier: Modifier, onDismiss: () -> Unit) {
    BasicAlertDialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = modifier.fillMaxWidth()) {
            Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
                end = dimensionResource(R.dimen.margin_standard),
                top = dimensionResource(R.dimen.margin_small_minus)).align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.daily_activity_level))
            Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
                end = dimensionResource(R.dimen.margin_standard),
                top = dimensionResource(R.dimen.margin_small_minus)).align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.daily_activity_level_explanation))
            TextButton(modifier = modifier.align(Alignment.End).padding(end = dimensionResource(R.dimen.margin_standard),
                top = dimensionResource(R.dimen.margin_standard),
                bottom = dimensionResource(R.dimen.margin_standard)), onClick = { onDismiss() }) {
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
            Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
                end = dimensionResource(R.dimen.margin_standard),
                top = dimensionResource(R.dimen.margin_small_minus)).align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.diet_fat_percent))
            Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
                end = dimensionResource(R.dimen.margin_standard),
                top = dimensionResource(R.dimen.margin_small_minus)).align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.fat_percent_explanation))
            TextButton(modifier = modifier.align(Alignment.End).padding(end = dimensionResource(R.dimen.margin_standard),
                top = dimensionResource(R.dimen.margin_standard),
                bottom = dimensionResource(R.dimen.margin_standard)), onClick = { onDismiss() }) {
                Text(text = stringResource(id = R.string.ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MacrosCalculatorScreenPreview() {
    MacrosCalculatorScreen(modifier = Modifier, navController = rememberNavController())
}