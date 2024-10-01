package com.cuinsolutions.macrosmanager

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cuinsolutions.macrosmanager.utils.Meal

@Composable
fun MealScreen(modifier: Modifier, navController: NavHostController, editMeal: Meal? = null, addMealViewModel: AddMealViewModel = hiltViewModel()) {
    Scaffold(modifier = modifier.navigationBarsPadding(),
        topBar = { MacrosManagerOptionsMenuAppBar(modifier = modifier, navController = navController, titleResourceId = if (editMeal == null) R.string.add_meal else R.string.edit_meal) },
        floatingActionButton = { MealSaveFAB(modifier = modifier, navController = navController, addMealViewModel = addMealViewModel) },
        bottomBar = { BannerAdview() }) {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)
            .scrollable(
                enabled = true,
                state = rememberScrollState(),
                orientation = Orientation.Vertical
            )) {
            MealNameTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealCaloriesTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealCarbsTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealFatsTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealProteinTextField(modifier = modifier, addMealViewModel = addMealViewModel)
            MealServingSizeTextField(modifier = modifier, addMealViewModel = addMealViewModel)
        }
    }
}

@Composable
fun MealNameTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {

    TextField(value = addMealViewModel.mealName,
        onValueChange = { mealName -> addMealViewModel.updateMealName(mealName) },
        label = { stringResource(id = R.string.meal_name) })
}

@Composable
fun MealCaloriesTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {
    TextField(value = addMealViewModel.mealCalories,
        onValueChange = { mealCalories -> addMealViewModel.updateMealCalories(mealCalories) },
        label = { stringResource(id = R.string.calories) })
}

@Composable
fun MealCarbsTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {
    TextField(value = addMealViewModel.mealCarbs,
        onValueChange = { mealCarbs -> addMealViewModel.updateMealCarbs(mealCarbs) },
        label = { stringResource(id = R.string.carbs) })
}

@Composable
fun MealFatsTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {
    TextField(value = addMealViewModel.mealFats,
        onValueChange = { mealFat -> addMealViewModel.updateMealFats(mealFat) },
        label = { stringResource(id = R.string.fat) })
}

@Composable
fun MealProteinTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {
    TextField(value = addMealViewModel.mealProtein,
        onValueChange = { mealProtein -> addMealViewModel.updateMealProtein(mealProtein) },
        label = { stringResource(id = R.string.protein) })
}

@Composable
fun MealServingSizeTextField(modifier: Modifier, addMealViewModel: AddMealViewModel) {
    TextField(value = addMealViewModel.mealServings,
        onValueChange = { mealServingSize -> addMealViewModel.updateMealServings(mealServingSize) },
        label = { stringResource(id = R.string.servings_size) })
}

@Composable
fun MealSaveFAB(modifier: Modifier, navController: NavHostController, addMealViewModel: AddMealViewModel) {
    FloatingActionButton(onClick = {
        //addMealViewModel.saveMeal()
        navController.popBackStack()
    }) {
        Icon(painterResource(R.drawable.ic_save), contentDescription = stringResource(id = R.string.save))
    }
}

@Preview
@Composable
fun AddMealScreenPreview() {
    MealScreen(modifier = Modifier, navController = rememberNavController())
}