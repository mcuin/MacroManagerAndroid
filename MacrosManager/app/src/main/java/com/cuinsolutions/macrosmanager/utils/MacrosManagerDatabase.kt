package com.cuinsolutions.macrosmanager.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database (
    entities = [UserInfo::class, CalculatorOptions::class, Macros::class, Meal::class],
    version = 1
)

@TypeConverters(RoomTypeConverters::class)
abstract class MacrosManagerDatabase: RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
    abstract fun calculatorOptionsDao(): CalculatorOptionsDao
    abstract fun macrosDao(): MacrosDao
    abstract fun mealsDao(): MealsDao
}

class RoomTypeConverters {

    val macrosListType = object : TypeToken<List<Macro>>() {}.type
    val mealListType = object : TypeToken<List<Meal>>() {}.type

    @TypeConverter
    fun convertMacrosListToJSONString(macrosList: List<Macro>): String = Gson().toJson(macrosList)

    @TypeConverter
    fun convertJSONStringToMacrosList(jsonString: String): List<Macro> = Gson().fromJson(jsonString, macrosListType)

    @TypeConverter
    fun convertMealListToJSONString(mealList: List<Meal>): String = Gson().toJson(mealList)

    @TypeConverter
    fun convertJSONStringToMealList(jsonString: String): List<Meal> = Gson().fromJson(jsonString, mealListType)
}

@Module
@InstallIn(SingletonComponent::class)
object MacrosManagerDatabaseModule {

    @Singleton
    @Provides
    fun provideMacrosManagerDatabase(@ApplicationContext app: Context): MacrosManagerDatabase {
        return Room.databaseBuilder(
            app,
            MacrosManagerDatabase::class.java,
            "macros_manager_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserInfoDao(db: MacrosManagerDatabase) = db.userInfoDao()

    @Singleton
    @Provides
    fun provideCalculatorOptionsDao(db: MacrosManagerDatabase) = db.calculatorOptionsDao()

    @Singleton
    @Provides
    fun provideMacros(db: MacrosManagerDatabase) = db.macrosDao()

    @Singleton
    @Provides
    fun provideMealsDao(db: MacrosManagerDatabase) = db.mealsDao()
}