package com.cuinsolutions.macrosmanager

import androidx.lifecycle.ViewModel
import java.util.Date

class MacrosCalculatorViewModel: ViewModel() {

    private val centimeterFactor = 30.48
    private val kilogramsFactor = 0.455

    fun heightImperialToMetric(foot: Int, inches: Double) : Double {

        val totalInches = (foot * 12) + inches

        return totalInches * centimeterFactor
    }

    fun weightStoneToMetric(stone: Int, pounds: Double): Double {

        val totalPounds = (stone * 14) + pounds

        return totalPounds * kilogramsFactor
    }

    fun weightImperialToMetric(pounds: Double): Double {

        return pounds * kilogramsFactor
    }

    fun calculate(cm: Double, kg: Double, settingsInfo: UserInfo) {

        val userPreferences = PreferencesManager().getInstance()

        dietFatPercent = fatPercentageEditText.text.toString().toDouble()

        var bmr = 0.0
        val tdee: Double
        val calories: Double
        val protein: Double
        val proteinCalories: Double
        val fat: Double
        val fatCalories: Double
        val carbs: Double
        val carbsCalories: Double
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")//SimpleDateFormat()
        val age = if (Date().month >= settingsInfo.birthDate) {
            Date().year - settingsInfo.
        } else {
            Date().year - settingsInfo.year - 1//(java.util.concurrent.TimeUnit.DAYS.convert(dateFormat.parse(DateTime.now().toString()).time -
        }
        //dateFormat.format(birthDate), java.util.concurrent.TimeUnit.MILLISECONDS) / 365)

        when (gender) {

            "male" -> {

                bmr = (10 * kg) + (6.25 * cm) - ((5 * age) + 5)
            }

            "female" -> {

                bmr = (10 * kg) + (6.25 * cm) - ((5 * age) - 161)
            }
        }

        when (dailyActivity) {

            "very light" -> {

                tdee = bmr * 1.20
            }

            "light" -> {

                tdee = bmr * 1.45
            }

            "moderate" -> {

                tdee = bmr * 1.55
            }

            "heavy" -> {

                tdee = bmr * 1.75
            }

            "very heavy" -> {

                tdee = 2.00
            }

            else -> {

                tdee = bmr
            }
        }

        when (goal) {

            "weightLossSuggested" -> {

                calories = tdee - (tdee * 0.15)
            }

            "weightLossAggressive" -> {

                calories = tdee - (tdee * 0.20)
            }

            "weightLossReckless" -> {

                calories = tdee - (tdee * 0.25)
            }

            "maintain" -> {

                calories = tdee
            }

            "bulkingSuggested" -> {

                calories = tdee + (tdee * 0.05)
            }

            "bulkingAggressive" -> {

                calories = tdee + (tdee * 0.10)
            }

            "bulkingReckless" -> {

                calories = tdee + (tdee * 0.15)
            }

            else -> {

                calories = tdee
            }
        }

        when (physicalActivityLifestyle) {

            "sedentaryAdult" -> {

                protein = pounds * 0.4
            }

            "recreationalExerciserAdult" -> {

                protein = pounds * 0.75
            }

            "competitiveAthleteAdult" -> {

                protein = pounds * 0.90
            }

            "buildingMuscleAdult" -> {

                protein = pounds * 0.90
            }

            "dietingAthlete" -> {

                protein = pounds * 0.90
            }

            "growingAthleteTeenager" -> {

                protein = pounds * 1.0
            }

            else -> {

                protein = pounds * 0.4
            }
        }

        proteinCalories = protein * 4

        fatCalories = calories * (fatPercentageEditText.text.toString().toDouble() / 100)
        fat = fatCalories / 9

        carbsCalories = calories - (proteinCalories + fatCalories)
        carbs = carbsCalories / 4

        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userId = currentUser.uid
            val db = fireStore.collection("users").document(userId)
            val updateData = hashMapOf("pounds" to pounds, "kg" to kg, "stone" to stone, "calories" to calories.toInt(), "carbs" to carbs.toInt(), "fat" to fat.toInt(),
                "protein" to protein.toInt(), "dietFatPercent" to dietFatPercent, "dailyActivity" to dailyActivity, "physicalActivityLifestyle" to physicalActivityLifestyle,
                "goal" to goal)
            db.update(updateData).addOnSuccessListener {
                Toast.makeText(requireContext(), "Your data has been successfully updated.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "There was an issue updating the data. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        } else {

            val createUserDialog = AlertDialog.Builder(requireContext())

            createUserDialog.setTitle("Create Account").setMessage("Would you like to create an account to " +
                    "store your settings and macros?").setPositiveButton("OK") { dialog, which ->

            }
                .setNegativeButton("No Thanks", { dialog, which ->

                })

            createUserDialog.show()
        }
    }
}