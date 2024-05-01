package com.cuinsolutions.macrosmanager

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.cuinsolutions.macrosmanager.databinding.FragmentAddMealBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddMealFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentAddMealBinding
    private val macrosManagerViewModel: MacrosManagerViewModel by activityViewModels()
    private val viewModel: AddMealViewModel by viewModels()
    private val addMealArgs by navArgs<AddMealFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_meal, container, false)

        if (addMealArgs.mealId != -1) {
            val meal = macrosManagerViewModel.currentMeals.first { meal -> meal.id == id }
            binding.addMealNameEdit.setText(meal.mealName)
            binding.addMealServingEdit.setText(meal.servingSize.toString())
            binding.addMealCaloriesEdit.setText(meal.mealCalories.toString())
            binding.addMealCarbsEdit.setText(meal.mealCarbs.toString())
            binding.addMealFatEdit.setText(meal.mealFats.toString())
            binding.addMealProteinEdit.setText(meal.mealProtein.toString())
        }

        return binding.root
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_meal_save -> {
                if (addMealArgs.mealId != -1) {
                    macrosManagerViewModel.updateMeal(viewModel.editMeal(binding.addMealNameEdit.text.toString(), binding.addMealServingEdit.text.toString().toDouble(),
                        binding.addMealCaloriesEdit.text.toString().toDouble(), binding.addMealCarbsEdit.text.toString().toDouble(),
                        binding.addMealFatEdit.text.toString().toDouble(), binding.addMealProteinEdit.text.toString().toDouble(),
                        macrosManagerViewModel.currentMeals.first { meal -> meal.id == addMealArgs.mealId }))
                } else {
                    macrosManagerViewModel.currentMeals.add(viewModel.addMeal(binding.addMealNameEdit.text.toString(), binding.addMealServingEdit.text.toString().toDouble(),
                        binding.addMealCaloriesEdit.text.toString().toDouble(), binding.addMealCarbsEdit.text.toString().toDouble(),
                        binding.addMealFatEdit.text.toString().toDouble(), binding.addMealProteinEdit.text.toString().toDouble()))
                    macrosManagerViewModel.saveMeals()
                }
            }
            R.id.add_meal_remove -> {
                macrosManagerViewModel.removeMeal(addMealArgs.mealId)
            }
        }
    }
}