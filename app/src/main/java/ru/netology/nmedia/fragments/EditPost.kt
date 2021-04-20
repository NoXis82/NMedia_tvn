package ru.netology.nmedia.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class EditPost : Fragment() {
    companion object {
        var Bundle.authorEdit: String? by StringArg
        var Bundle.publishedEdit: String? by StringArg
        var Bundle.contentEdit: String? by StringArg
    }
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentEditPostBinding.inflate(layoutInflater).apply {
        author.text = arguments?.authorEdit
        published.text = arguments?.publishedEdit
        arguments?.contentEdit?.let(editContent::setText)
        editContent.requestFocus()
        btnSavePost.setOnClickListener {
            with(editContent) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_empty_post),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                viewModel.changeContent(editContent.text.toString())
                viewModel.savePost()
                findNavController().navigate(R.id.action_editPost_to_feedFragment)
            }
        }
    }.root
}