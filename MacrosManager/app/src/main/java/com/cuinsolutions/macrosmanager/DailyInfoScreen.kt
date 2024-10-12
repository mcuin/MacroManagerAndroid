package com.cuinsolutions.macrosmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cuinsolutions.macrosmanager.utils.Macro
import com.cuinsolutions.macrosmanager.utils.Meal

@Composable
fun DailyInfoScreen(modifier: Modifier, navController: NavHostController, viewModel: DailyInfoViewModel = hiltViewModel()) {

    Scaffold(modifier = modifier.navigationBarsPadding(), topBar = { MacrosManagerOptionsMenuAppBar(
        modifier = modifier,
        titleResourceId = R.string.macros,
        navController = navController
    )},
        floatingActionButton = { AddMealFAB(modifier = modifier, navController = navController) },
        bottomBar = { BottomNavigationBar(modifier = modifier, navController = navController) }) {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)) {

            val state by viewModel.states.collectAsStateWithLifecycle()
            val meals by viewModel.mealsList.collectAsStateWithLifecycle()

            when (val macros = state) {
                is DailyInfoUiState.Success -> {
                    DailyMacrosGrid(modifier = modifier, macros = macros.macros)
                    DailyMealsList(modifier = modifier.weight(1f), meals = meals, navController = navController)
                    BannerAdview(stringResource(id = R.string.daily_info_ad_unit_id))
                }
                is DailyInfoUiState.Loading -> {}
            }
        }
    }
}

@Composable
fun DailyMacrosGrid(modifier: Modifier, macros: List<Macro>) {
    LazyVerticalGrid(columns = GridCells.Fixed(count = 4), modifier = modifier) {
        items(macros) { macro ->
            DailyMacrosGridCell(modifier = modifier, macro = macro)
        }
    }
}

@Composable
fun DailyMacrosGridCell(modifier: Modifier, macro: Macro) {
    Column(modifier = modifier) {
        Text(modifier = modifier
            .align(Alignment.CenterHorizontally), text = macro.name)
        Text(modifier = modifier
            .align(Alignment.CenterHorizontally), text = stringResource(id = R.string.macros_daily, macro.current, macro.daily))
    }
}

@Composable
fun DailyMealsList(modifier: Modifier, meals: List<Meal>, navController: NavHostController) {
    LazyColumn(modifier = modifier.fillMaxWidth().padding(top = dimensionResource(R.dimen.margin_standard))) {
        items(meals) { meal ->
            DailyMealsListItem(modifier = modifier, meal = meal, navController = navController)
        }
    }
}

@Composable
fun DailyMealsListItem(modifier: Modifier, meal: Meal, navController: NavHostController) {

    Card (modifier = modifier.padding(top = dimensionResource(R.dimen.margin_small), start = dimensionResource(R.dimen.margin_standard), end = dimensionResource(R.dimen.margin_standard)), shape = RoundedCornerShape(10.dp),
        onClick = {
            navController.navigate(
                route = Screens.Meal.route.replace(
                    "{mealId}",
                    meal.id.toString()
                )
            )
        }) {
        Row {
            Text(
                modifier = modifier
                .padding(
                    top = dimensionResource(R.dimen.margin_small),
                    start = dimensionResource(R.dimen.margin_small)
                ),
                text = meal.mealName
            )
            Text(
                modifier = modifier
                .padding(top = dimensionResource(R.dimen.margin_small),
                end = dimensionResource(R.dimen.margin_small)),
                text = stringResource(R.string.meal_card_calories, meal.mealCalories * meal.servingSize)
            )
        }
        Row {
            Text(modifier = modifier.padding(top = dimensionResource(R.dimen.margin_small), start = dimensionResource(R.dimen.margin_small)), text = stringResource(R.string.meal_card_servings, meal.servingSize))
            Text(modifier = modifier.padding(top = dimensionResource(R.dimen.margin_small), end = dimensionResource(R.dimen.margin_small)), text = stringResource(R.string.meal_card_carbs, meal.mealCarbs * meal.servingSize))
        }

        Row {
            Text(modifier = modifier.padding(top = dimensionResource(R.dimen.margin_small), start = dimensionResource(R.dimen.margin_small), bottom = dimensionResource(R.dimen.margin_small)), text = stringResource(R.string.meal_card_fat, meal.mealFats * meal.servingSize))
            Text(modifier = modifier.padding(top = dimensionResource(R.dimen.margin_small), end = dimensionResource(R.dimen.margin_small), bottom = dimensionResource(R.dimen.margin_small)), text = stringResource(R.string.meal_card_protein, meal.mealProtein * meal.servingSize))
        }
    }
}

@Composable
fun AddMealFAB(modifier: Modifier, navController: NavHostController) {
    FloatingActionButton(modifier = modifier.padding(bottom = dimensionResource(R.dimen.banner_ad_padding)), containerColor = MaterialTheme.colorScheme.primary, onClick = { navController.navigate(route = Screens.Meal.route) }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.add_meal))
    }
}

@Preview(showBackground = true)
@Composable
fun DailyInfoScreenPreview() {
    DailyInfoScreen(modifier = Modifier, navController = rememberNavController())
}