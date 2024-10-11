package com.cuinsolutions.macrosmanager.utils

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userInfo: UserInfo)

    @Query("SELECT * FROM user_info")
    fun getUserInfo(): Flow<UserInfo?>
}

@Dao
interface CalculatorOptionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calculatorOptions: CalculatorOptions)

    @Query("SELECT * FROM calculator_options")
    fun getCalculatorOptions(): Flow<CalculatorOptions?>
}

@Dao
interface MacrosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(macros: Macros)

    @Query("SELECT * FROM macros")
    fun getMacros(): Flow<Macros?>
}

@Dao
interface MealsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: Meal)

    @Query("SELECT * FROM meals")
    fun getMeals(): Flow<List<Meal>?>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: Int): Meal

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMealById(id: Int)

    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()
}