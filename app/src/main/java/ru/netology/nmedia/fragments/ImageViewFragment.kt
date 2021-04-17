package ru.netology.nmedia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentImageViewBinding
import ru.netology.nmedia.utils.StringArg

@AndroidEntryPoint
class ImageViewFragment : Fragment() {
    companion object {
        var Bundle.urlImage: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentImageViewBinding.inflate(layoutInflater)
        binding.apply {
            Glide.with(ivImagePick)
                .load("http://172.20.10.7:9999/media/${arguments?.urlImage}")
                .placeholder(R.drawable.ic_attach_error_48)
                .timeout(10_000)
                .into(ivImagePick)
        }
        return binding.root
    }
}