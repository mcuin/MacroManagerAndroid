package com.cuinsolutions.macrosmanager.utils

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MacrosRepository @Inject constructor(val macrosDao: MacrosDao) {

    val macros: Flow<Macros?> = macrosDao.getMacros()

    suspend fun insertMacros(macros: Macros) {
        macrosDao.insert(macros)
    }
}