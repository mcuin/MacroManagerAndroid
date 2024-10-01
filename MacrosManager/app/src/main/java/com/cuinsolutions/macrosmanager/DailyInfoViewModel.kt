package com.cuinsolutions.macrosmanager

import androidx.lifecycle.ViewModel
import com.cuinsolutions.macrosmanager.utils.Macro
import com.cuinsolutions.macrosmanager.utils.Macros
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DailyInfoViewModel @Inject constructor() : ViewModel() {

    private val _macros = MutableStateFlow(Macros(listOf(Macro("calories", 0.0, 2000), Macro("carbs", 0.0, 20), Macro("fats", 0.0, 2), Macro("protein", 0.0, 2))))
    val macros = _macros.asStateFlow()
}