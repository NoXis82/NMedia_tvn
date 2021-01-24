package ru.netology.nmedia

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentAddNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class AddNewPost : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAddNewPostBinding.inflate(layoutInflater)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.isHandledBackPressed = binding.newContent.text.toString()
                    findNavController().popBackStack(R.id.feedFragment, false)
                }
            })
        binding.newContent.requestFocus()
        binding.newContent.setText(viewModel.isHandledBackPressed)
        binding.btnSaveNewPost.setOnClickListener {
            with(binding.newContent) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_empty_post),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                viewModel.changeContent(binding.newContent.text.toString())
                viewModel.savePost()
                viewModel.isHandledBackPressed = ""
                AndroidUtils.hideKeyboard(requireView())
                viewModel.postCreated.observe(viewLifecycleOwner) {
                    viewModel.loadPosts()
                    findNavController().navigateUp()
                }
            }
        }
        return binding.root
    }

}