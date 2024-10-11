package com.cuinsolutions.macrosmanager

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cuinsolutions.macrosmanager.utils.Meal

@Composable
fun MealScreen(modifier: Modifier, navController: NavHostController, mealId: Int, addMealViewModel: AddMealViewModel = hiltViewModel()) {
    Scaffold(modifier = modifier.navigationBarsPadding().imePadding(),
        topBar = { MacrosManagerOptionsMenuAppBar(modifier = modifier, navController = navController, titleResourceId = if (mealId < 0) R.string.add_meal else R.string.edit_meal) },
        floatingActionButton = { MealSaveFAB(modifier = modifier, navController = navController, addMealViewModel = addMealViewModel) },
        bottomBar = { BannerAdview() }) {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)
            .verticalScroll(
                enabled = true,
                state = rememberScrollState()
            )) {
            MealNameTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealCaloriesTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealCarbsTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealFatsTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealProteinTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealServingSizeTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            if (mealId >= 0) {
                addMealViewModel.loadMeal(mealId)
                DeleteMealButton(modifier = modifier.align(Alignment.End), addMealViewModel = addMealViewModel, mealId = mealId, navController = navController)
            }
        }
    }
}

@Composable
fun MealNameTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {

    val focusManager = LocalFocusManager.current

    TextField(modifier = modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.margin_standard), vertical = dimensionResource(R.dimen.margin_small)),
        value = addMealViewModel.mealName,
        onValueChange = { mealName -> addMealViewModel.updateMealName(mealName) },
        label = { Text(stringResource(id = R.string.meal_name)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
}

@Composable
fun MealCaloriesTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {

    val focusManager = LocalFocusManager.current

    TextField(modifier = modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.margin_standard), vertical = dimensionResource(R.dimen.margin_small)),
        value = addMealViewModel.mealCalories,
        onValueChange = { mealCalories -> addMealViewModel.updateMealCalories(mealCalories) },
        label = { Text(stringResource(id = R.string.calories)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        singleLine = true,
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
}

@Composable
fun MealCarbsTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {

    val focusManager = LocalFocusManager.current

    TextField(modifier = modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.margin_standard), vertical = dimensionResource(R.dimen.margin_small)),
        value = addMealViewModel.mealCarbs,
        onValueChange = { mealCarbs -> addMealViewModel.updateMealCarbs(mealCarbs) },
        label = { Text(stringResource(id = R.string.carbs)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
}

@Composable
fun MealFatsTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {

    val focusManager = LocalFocusManager.current

    TextField(modifier = modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.margin_standard), vertical = dimensionResource(R.dimen.margin_small)),
        value = addMealViewModel.mealFats,
        onValueChange = { mealFat -> addMealViewModel.updateMealFats(mealFat) },
        label = { Text(stringResource(id = R.string.fat)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
}

@Composable
fun MealProteinTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {

    val focusManager = LocalFocusManager.current

    TextField(modifier = modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.margin_standard), vertical = dimensionResource(R.dimen.margin_small)),
        value = addMealViewModel.mealProtein,
        onValueChange = { mealProtein -> addMealViewModel.updateMealProtein(mealProtein) },
        label = { Text(stringResource(id = R.string.protein)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
}

@Composable
fun MealServingSizeTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {

    val focusManager = LocalFocusManager.current

    TextField(modifier = modifier.fillMaxWidth().padding(horizontal = dimensionResource(R.dimen.margin_standard), vertical = dimensionResource(R.dimen.margin_small)),
        value = addMealViewModel.mealServings,
        onValueChange = { mealServingSize -> addMealViewModel.updateMealServings(mealServingSize) },
        label = { Text(stringResource(id = R.string.servings_size)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
}

@Composable
fun DeleteMealButton(modifier: Modifier, addMealViewModel: AddMealViewModel, mealId: Int, navController: NavHostController) {
    Button(modifier = modifier.padding(top = dimensionResource(R.dimen.margin_standard), end = dimensionResource(R.dimen.margin_standard)), onClick = {
        addMealViewModel.deleteMeal(mealId)
        navController.popBackStack()
    }) {
        Text(text = stringResource(id = R.string.delete_meal))
    }
}

@Composable
fun MealSaveFAB(modifier: Modifier, navController: NavHostController, addMealViewModel: AddMealViewModel) {
    FloatingActionButton(onClick = {
        addMealViewModel.saveMeal()
        navController.popBackStack()
    }) {
        Icon(painterResource(R.drawable.ic_save), contentDescription = stringResource(id = R.string.save))
    }
}

@Preview
@Composable
fun AddMealScreenPreview() {
    MealScreen(modifier = Modifier, mealId = -1, navController = rememberNavController())
}