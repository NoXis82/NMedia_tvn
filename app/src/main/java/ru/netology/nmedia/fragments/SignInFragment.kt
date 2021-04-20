package ru.netology.nmedia.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.SignInViewModel

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private val viewModel: SignInViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSignInBinding.inflate(layoutInflater).apply {
        btnSignIn.setOnClickListener {
            with(etLogin) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_login),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }
            with(etPass) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_pass),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }
            viewModel.getUserLogin(etLogin.text.toString(), etPass.text.toString())
            findNavController().navigateUp()
        }
    }.root

}