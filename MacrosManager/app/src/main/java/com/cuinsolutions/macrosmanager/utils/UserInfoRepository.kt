package com.cuinsolutions.macrosmanager.utils

import androidx.room.Insert
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserInfoRepository @Inject constructor(private val userInfoDao: UserInfoDao) {

    val userInfo: Flow<UserInfo?> = userInfoDao.getUserInfo()

    suspend fun insertUserInfo(userInfo: UserInfo) {
        userInfoDao.insert(userInfo)
    }
}