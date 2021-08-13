package com.mriofrio.rideapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mriofrio.rideapp.MainActivity
import com.mriofrio.rideapp.R
import com.mriofrio.rideapp.databinding.FragmentLoginBinding
import com.mriofrio.rideapp.other.LoginRegisterViewModelFactory
import com.mriofrio.rideapp.viewmodel.LoginRegisterViewModel

/**
 * Main Screen shown to prompt user to log in into their account.
 * TODO: Add logic to cache user log in information for a better user experience.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginRegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.application?.let { app ->
            loginViewModel = ViewModelProvider(
                this,
                LoginRegisterViewModelFactory(app)
            )
                .get(LoginRegisterViewModel::class.java)
        }

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener { login() }
        binding.txtLinkToRegister.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun login() {
        loginViewModel.login(
            binding.fieldEmailAddress.text.toString(),
            binding.fieldPassword.text.toString()
        ).let { result ->
            // if the log in attempt fails (returns false) then validation failed.
            if (result) {
                subscribeToObservers()
            } else {
                Toast.makeText(
                    context,
                    "Email or password not entered.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.fieldEmailAddress.requestFocus()
            }
        }
    }

    private fun subscribeToObservers() {
        loginViewModel.isUserLoggedIn.observe(viewLifecycleOwner, Observer {
            moveToMainActivity(it)
        })
    }

    private fun moveToMainActivity(loggedIn: Boolean) {
        if (loggedIn) {
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }


}