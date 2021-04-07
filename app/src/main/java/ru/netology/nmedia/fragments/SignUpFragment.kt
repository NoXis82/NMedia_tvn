package ru.netology.nmedia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignUpBinding.inflate(layoutInflater)
        binding.apply {
            val nameUser = etName.text
            val loginUser = etLogin.text
            val passUser = etPass.text
            val retryPass = etPassRetry.text
            btnSignUp.setOnClickListener {
                if (nameUser.toString().trim().isEmpty() ||
                    loginUser.toString().trim().isEmpty() ||
                    passUser.toString().trim().isEmpty()
                ) {
                    Toast.makeText(requireContext(), R.string.error_validate_value, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (passUser.toString().trim() != retryPass.toString().trim()) {
                    Toast.makeText(requireContext(), R.string.error_validate_pass, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.regUserData(
                    loginUser.toString(),
                    passUser.toString(),
                    nameUser.toString()
                )
                findNavController().navigateUp()
            }
        }
        return binding.root
    }

}