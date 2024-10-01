package com.cuinsolutions.macrosmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cuinsolutions.macrosmanager.utils.Macro
import com.cuinsolutions.macrosmanager.utils.Macros

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

            val macros by viewModel.macros.collectAsStateWithLifecycle()

            DailyMacrosGrid(modifier = modifier, macros = macros)
            DailyMealsList(modifier = modifier.weight(1f))
            BannerAdview()
        }
    }
}

@Composable
fun DailyMacrosGrid(modifier: Modifier, macros: Macros) {
    LazyVerticalGrid(columns = GridCells.Fixed(count = 4), modifier = modifier) {
        items(macros.macros) { macro ->
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
fun DailyMealsList(modifier: Modifier) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {

    }
}

@Composable
fun AddMealFAB(modifier: Modifier, navController: NavHostController) {
    FloatingActionButton(onClick = { navController.navigate(route = Screens.Meal.route) }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.add_meal))
    }
}

@Preview(showBackground = true)
@Composable
fun DailyInfoScreenPreview() {
    DailyInfoScreen(modifier = Modifier, navController = rememberNavController())
}