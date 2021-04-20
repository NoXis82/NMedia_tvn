package ru.netology.nmedia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentImageViewBinding
import ru.netology.nmedia.utils.StringArg

class ImageViewFragment : Fragment() {
    companion object {
        var Bundle.urlImage: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentImageViewBinding.inflate(layoutInflater).apply {
        Glide.with(ivImagePick)
            .load("http://192.168.0.106:9999/media/${arguments?.urlImage}")
            .placeholder(R.drawable.ic_attach_error_48)
            .timeout(10_000)
            .into(ivImagePick)
    }.root
}