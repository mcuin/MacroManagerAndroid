package com.cuinsolutions.macrosmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun MacrosManagerApp(navController: NavHostController = rememberNavController()) {
    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(modifier = Modifier, navController = navController, startDestination = BottomNavigationItem.Macros.route) {
            composable(BottomNavigationItem.Macros.route) {
                DailyInfoScreen(modifier = Modifier, navController = navController)
            }
            composable(BottomNavigationItem.Calculator.route) {
                MacrosCalculatorScreen(modifier = Modifier, navController = navController)
            }
            composable(Screens.Settings.route) {
                SettingsScreen(modifier = Modifier, navController = navController)
            }
            composable(route = Screens.Meal.route, arguments = listOf(
                navArgument("mealId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )) {
                MealScreen(modifier = Modifier, mealId = it.arguments?.getInt("mealId") ?: -1, navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacrosManagerOptionsMenuAppBar(modifier: Modifier, titleResourceId: Int, navController: NavHostController) {

    var optionsExpanded by remember { mutableStateOf(false) }

    TopAppBar(modifier = modifier, title = { Text(text = stringResource(id = titleResourceId)) },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton( onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }
            }
        },
        actions = {

        IconButton(onClick = { optionsExpanded = !optionsExpanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "")
        }

        DropdownMenu(expanded = optionsExpanded, onDismissRequest = { optionsExpanded = false }) {

            DropdownMenuItem(text = { Text(text = stringResource(id = R.string.settings)) },
                onClick = {
                    optionsExpanded = false
                    navController.navigate(Screens.Settings.route)
                })
        }
    })
}

@Composable
fun BottomNavigationBar(modifier: Modifier, navController: NavHostController) {

    val items = listOf(
        BottomNavigationItem.Macros,
        BottomNavigationItem.Calculator
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(modifier = modifier.fillMaxWidth()) {

        items.forEach { item ->
            BottomNavigationItem(icon = { item.icon },
                label = { Text(text = stringResource(id = item.titleResourceId)) },
                selected = currentRoute == item.route, onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route)
                    }
                })
        }
    }
}

@Composable
fun BannerAdview() {
    AndroidView(factory = { context ->
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = "ca-app-pub-3940256099942544/6300978111"
            loadAd(AdRequest.Builder().build())
        }
    }, modifier = Modifier
        .fillMaxWidth().padding(top = dimensionResource(R.dimen.margin_standard)))
}

@Composable
fun MacrosManagerRadioButton(modifier: Modifier, title: String, isSelected: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_standard),
            top = dimensionResource(R.dimen.margin_smallest)), selected = isSelected, onClick = null)
        Text(modifier = modifier.padding(start = dimensionResource(R.dimen.margin_small_minus)), text = title)
    }
}

sealed class Screens(val route: String) {
    data object Settings : Screens("settings")
    data object Meal: Screens("meal?mealId={mealId}")
}

sealed class BottomNavigationItem(val route: String, val titleResourceId: Int, val icon: Int) {
    data object Macros : BottomNavigationItem("macros", R.string.macros, R.drawable.ic_macros)
    data object Calculator : BottomNavigationItem("calculator", R.string.calculator, R.drawable.ic_calculator)
}