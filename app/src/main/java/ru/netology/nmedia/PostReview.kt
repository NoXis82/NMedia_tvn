package ru.netology.nmedia

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.EditPost.Companion.authorEdit
import ru.netology.nmedia.EditPost.Companion.contentEdit
import ru.netology.nmedia.EditPost.Companion.publishedEdit
import ru.netology.nmedia.databinding.FragmentPostReviewBinding
import ru.netology.nmedia.model.getCreateReadableMessageError
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel


class PostReview : Fragment() {

    companion object {
        var Bundle.idPost: String? by StringArg
        var Bundle.author: String? by StringArg
        var Bundle.published: String? by StringArg
        var Bundle.content: String? by StringArg
        var Bundle.videoUrl: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostReviewBinding.inflate(layoutInflater)
        binding.content.text = arguments?.content
        binding.author.text = arguments?.author
        binding.published.text = arguments?.published
        binding.menuPost.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.option_menu_post)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.postRemove -> {
                            arguments?.idPost?.toLong()?.let { id -> viewModel.removePost(id) }
                            viewModel.state.observe(viewLifecycleOwner) { model ->
                                if (!model.loading && !model.errorVisible) {
                                    findNavController().navigateUp()
                                }
                              //  viewModel.postRemoveError.observe(viewLifecycleOwner) { error ->
//                                    Toast.makeText(
//                                        requireContext(),
//                                        error.getCreateReadableMessageError(resources),
//                                        Toast.LENGTH_SHORT
//                                    )
//                                        .show()
                              //  }
                            }
                            true
                        }
                        R.id.postEdit -> {
                            findNavController().navigate(R.id.action_postReview_to_editPost,
                                Bundle().apply {
                                    authorEdit = arguments?.author
                                    publishedEdit = arguments?.published
                                    contentEdit = arguments?.content
                                })
                            true
                        }
                        else -> false
                    }
                }
            }.show()

        }
        return binding.root
    }

}