package com.cuinsolutions.macrosmanager.utils

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CalculatorOptionsRepository @Inject constructor(private val calculatorOptionsDao: CalculatorOptionsDao) {

    val calculatorOptions: Flow<CalculatorOptions?> = calculatorOptionsDao.getCalculatorOptions()

    suspend fun updateCalculatorOptions(calculatorOptions: CalculatorOptions) {
        calculatorOptionsDao.insert(calculatorOptions)
    }

}