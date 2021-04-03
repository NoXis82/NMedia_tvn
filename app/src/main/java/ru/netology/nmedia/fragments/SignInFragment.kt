package ru.netology.nmedia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(layoutInflater)


        return binding.root
    }

}