package com.mriofrio.rideapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.mriofrio.rideapp.LoginRegisterActivity
import com.mriofrio.rideapp.R
import com.mriofrio.rideapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (user != null) {
            binding.userEmail.text =
                requireContext().getString(R.string.user_email_title, user.email)
        }
        binding.logOutButton.setOnClickListener { logOut() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logOut() {
        auth.signOut()
        startActivity(Intent(requireContext(), LoginRegisterActivity::class.java))
    }
}