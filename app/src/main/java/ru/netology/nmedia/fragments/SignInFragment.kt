package ru.netology.nmedia.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SingInViewModel

class SignInFragment : Fragment() {
    private val viewModel: SingInViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(layoutInflater)
        binding.btnSignIn.setOnClickListener {
            with(binding.etLogin) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_login),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }
            with(binding.etPass) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_pass),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }
            viewModel.getUserLogin(binding.etLogin.text.toString(), binding.etPass.text.toString())
            findNavController().navigateUp()
        }
        return binding.root
    }

}