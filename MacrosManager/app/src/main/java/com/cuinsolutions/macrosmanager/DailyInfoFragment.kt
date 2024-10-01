package com.cuinsolutions.macrosmanager

import android.app.AlertDialog
import android.icu.text.DecimalFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.cuinsolutions.macrosmanager.databinding.FragmentDailyInfoBinding
import com.cuinsolutions.macrosmanager.utils.MacroCell
import com.cuinsolutions.macrosmanager.utils.Macros
import kotlinx.coroutines.launch

class DailyInfoFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentDailyInfoBinding
    private val macrosManagerViewModel: MacrosManagerViewModel by activityViewModels()
    private val macrosAdapter by lazy { DailyMacrosGridViewAdapter() }
    private val decimalFormat = DecimalFormat("#.##")
    private val macrosCells by lazy { createMacrosCells(macrosManagerViewModel.currentUserMacros.value) }
    private val mealsAdapter by lazy { DailyMealsRecyclerViewAdapter() { id ->
        findNavController().navigate(DailyInfoFragmentDirections.navigateToAddMeal(id))
    }}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_info, container, false)

        binding.listener = this

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    macrosManagerViewModel.currentUserMacros.collect {
                        macrosAdapter.setMacroItems(createMacrosCells(it))
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    macrosManagerViewModel.userDeleted.collect {
                        val message = if (it) {
                            R.string.user_account_deleted
                        } else {
                            R.string.user_account_delete_error
                        }

                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.delete_account)
                            .setMessage(message)
                            .setPositiveButton(R.string.ok, null)
                            .show()
                    }
                }
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (macrosManagerViewModel.auth.currentUser != null) {
                    menuInflater.inflate(R.menu.action_bar_menu, menu)
                } else {
                    menuInflater.inflate(R.menu.no_user_menu, menu)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_sign_up -> findNavController().navigate(DailyInfoFragmentDirections.navigateToSignUp())
                    R.id.action_settings -> findNavController().navigate(DailyInfoFragmentDirections.navigateToSettings())
                    R.id.action_sign_in -> findNavController().navigate(DailyInfoFragmentDirections.navigateToSignIn())
                    R.id.action_delete_account -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.delete_account)
                            .setMessage(R.string.delete_account_description)
                            .setPositiveButton(R.string.delete) { dialog, _ ->
                                dialog.dismiss()
                                macrosManagerViewModel.deleteUser()
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .show()
                    }
                }

                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.STARTED)

        binding.dailyMacrosGridRecycler.adapter = macrosAdapter
        macrosAdapter.setMacroItems(macrosCells)
        binding.dailyFoodRecycler.adapter = mealsAdapter
        mealsAdapter.setMeals(macrosManagerViewModel.currentMeals)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.daily_add_meal_fab -> findNavController().navigate(DailyInfoFragmentDirections.navigateToAddMeal(-1))
        }
    }

    private fun createMacrosCells(macros: Macros): List<MacroCell> {
        return listOf(
            /*MacroCell(getString(R.string.calories),
            getString(R.string.macros_daily, decimalFormat.format(macros.currentCalories), macros.dailyCalories)),
            MacroCell(getString(R.string.carbs), getString(R.string.macros_daily, decimalFormat.format(macros.currentCarbs), macros.dailyCarbs)),
            MacroCell(getString(R.string.fat), getString(R.string.macros_daily, decimalFormat.format(macros.currentFats), macros.dailyFats)),
            MacroCell(getString(R.string.protein), getString(R.string.macros_daily, decimalFormat.format(macros.currentProtein), macros.dailyProtein))*/
        )
    }
}