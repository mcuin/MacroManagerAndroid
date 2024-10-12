package com.cuinsolutions.macrosmanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuinsolutions.macrosmanager.utils.UserInfo
import com.cuinsolutions.macrosmanager.utils.UserInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(val userInfoRepository: UserInfoRepository) : ViewModel() {

    var saveFabError by mutableStateOf(false)
    var userInfo by mutableStateOf(UserInfo())
    val states = userInfoRepository.userInfo.map { repoUserInfo ->
        if (repoUserInfo != null) {
            userInfo = repoUserInfo
            saveFabError = userInfo.birthMonth > -1 && userInfo.birthYear > -1
            SettingsScreenUIState.Success
        } else {
            userInfo = UserInfo()
            SettingsScreenUIState.Success
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SettingsScreenUIState.Loading)

    fun updateUserGender(genderId: Int) {
        userInfo = userInfo.copy(gender = genderId)
    }

    fun updateUserHeightMeasurement(heightMeasurementId: Int) {
        userInfo = userInfo.copy(heightMeasurement = heightMeasurementId)
    }

    fun updateUserWeightMeasurement(weightMeasurementId: Int) {
        userInfo = userInfo.copy(weightMeasurement = weightMeasurementId)
    }

    fun updateUserBirthInfo(updatedBirthMonth: Int, updatedBirthYear: Int) {
        userInfo = userInfo.copy(birthMonth = updatedBirthMonth, birthYear = updatedBirthYear)
    }

    fun saveSettings() {
        viewModelScope.launch {
            userInfoRepository.insertUserInfo(userInfo)
        }
    }

    fun validateSettings(): Boolean {
        return userInfo.birthMonth > -1 && userInfo.birthYear > -1
    }
}

sealed class SettingsScreenUIState {
    data object Loading : SettingsScreenUIState()
    data object Success : SettingsScreenUIState()
    data object Error : SettingsScreenUIState()
}