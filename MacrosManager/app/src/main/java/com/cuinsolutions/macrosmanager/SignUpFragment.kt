package com.cuinsolutions.macrosmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.cuinsolutions.macrosmanager.databinding.FragmentSignUpBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.HashMap

class SignUpFragment : Fragment(), OnClickListener {

    private lateinit var binding: FragmentSignUpBinding
    private val macrosManagerViewModel by activityViewModels<MacrosManagerViewModel>()
    private val signUpViewModel: SignUpViewModel by viewModels {
        SignUpViewModel.SignUpFactory(requireActivity().application, macrosManagerViewModel.auth, macrosManagerViewModel.fireStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.listener = this

        //val dailyMeals: Array<HashMap<String, Any>> = gson.fromJson(intent.getStringExtra("dailyMeals"), object : TypeToken<Array<HashMap<String, Any>>>() {}.type)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    signUpViewModel.signUpResult.collect {
                        if (it != null) {
                            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Account created successfully.", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_up_sign_up -> {
                when {
                    binding.signUpFirstNameEdit.text.toString().isBlank() -> Toast.makeText(requireContext(), "Please enter a valid first name.", Toast.LENGTH_SHORT).show()
                    binding.signUpLastNameEdit.text.toString().isBlank() -> Toast.makeText(requireContext(), "Please enter a valid last name.", Toast.LENGTH_SHORT).show()
                    binding.signUpEmailEdit.text.toString().isBlank() && !signUpViewModel.validEmail(binding.signUpEmailEdit.text.toString()) -> Toast.makeText(requireContext(), "Please enter a valid email.", Toast.LENGTH_SHORT).show()
                    binding.signUpPasswordEdit.text.toString().isBlank() -> Toast.makeText(requireContext(), "Please enter a password.", Toast.LENGTH_SHORT).show()
                    binding.signUpConfirmPasswordEdit.text.toString().isBlank() -> Toast.makeText(requireContext(), "Please enter confirm your password.", Toast.LENGTH_SHORT).show()
                    binding.signUpPasswordEdit.text.toString() != binding.signUpConfirmPasswordEdit.text.toString() -> Toast.makeText(requireContext(), "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show()
                    else -> {
                        signUpViewModel.signUp(binding.signUpEmailEdit.text.toString(), binding.signUpConfirmPasswordEdit.text.toString(), binding.signUpFirstNameEdit.text.toString(),
                            binding.signUpLastNameEdit.text.toString())
                    }
                }
            }
        }
    }
}