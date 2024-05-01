package com.cuinsolutions.macrosmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cuinsolutions.macrosmanager.databinding.FragmentSignInDialogBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SignInDialogFragment : DialogFragment(), OnClickListener {

    private lateinit var binding: FragmentSignInDialogBinding
    private val macrosManagerViewModel: MacrosManagerViewModel by activityViewModels()
    private val signInViewModel: SignInViewModel by viewModels {
        SignInViewModel.SignInFactory(macrosManagerViewModel.auth)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    signInViewModel.signInSuccess.collect {signInResult ->
                        if (!signInResult) {
                            val loginFailAlert = android.app.AlertDialog.Builder(requireContext())
                            loginFailAlert.setTitle(R.string.login_failed)
                                .setMessage(R.string.login_failed_description)
                                .setNeutralButton(R.string.ok) {
                                    dialog, _ ->
                                    dialog.dismiss()
                            }.show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in_dialog, container, false)

        binding.listener = this

        return binding.root
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.sign_in_sign_in -> signInViewModel.signIn(binding.signInEmailEdit.text.toString(),
                binding.signInPasswordEdit.text.toString())
            R.id.sign_in_cancel -> dialog?.cancel()
        }
    }
}