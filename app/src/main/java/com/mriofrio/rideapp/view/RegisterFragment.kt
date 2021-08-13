package com.mriofrio.rideapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mriofrio.rideapp.R
import com.mriofrio.rideapp.databinding.FragmentRegisterBinding
import com.mriofrio.rideapp.other.LoginRegisterViewModelFactory
import com.mriofrio.rideapp.viewmodel.LoginRegisterViewModel

/**
 * Fragment that allows the user to register an account through Firebase
 */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerViewModel: LoginRegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.application?.let { app ->
            registerViewModel = ViewModelProvider(
                this,
                LoginRegisterViewModelFactory(app)
            )
                .get(LoginRegisterViewModel::class.java)
        }

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSingUp.setOnClickListener { register(it) }
        binding.txtLinkToLogIn.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }

    }

    private fun register(view: View) {
        registerViewModel.register(
            binding.fieldEmailAddressRegister.text.toString(),
            binding.fieldPasswordRegister.text.toString()
        ).let { result ->
            // on successful registration, move to login fragment
            if (result) {
                view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            else {
                Toast.makeText(
                    context,
                    "Email or password not entered.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}