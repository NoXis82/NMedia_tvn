package ru.netology.nmedia.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.fragments.ImageViewFragment.Companion.urlImage
import ru.netology.nmedia.R
import ru.netology.nmedia.fragments.PostReview.Companion.author
import ru.netology.nmedia.fragments.PostReview.Companion.content
import ru.netology.nmedia.fragments.PostReview.Companion.idPost
import ru.netology.nmedia.fragments.PostReview.Companion.published
import ru.netology.nmedia.adapter.IOnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.fragments.EditPost.Companion.authorEdit
import ru.netology.nmedia.fragments.EditPost.Companion.contentEdit
import ru.netology.nmedia.fragments.EditPost.Companion.publishedEdit
import ru.netology.nmedia.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFeedBinding.inflate(layoutInflater).apply {
        var removeId = 0L
        var checkPost = Post()
        val adapter = PostsAdapter(object : IOnInteractionListener {

            override fun onLike(post: Post) {
                if (viewModel.checkSignIn()) {
                    checkPost = post
                    viewModel.like(post)
                } else {
                    dialogSign()
                }
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(
                    intent,
                    R.string.chooser_share_post.toString()
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(shareIntent)
            }

            override fun onRemove(post: Post) {
                removeId = post.id
                viewModel.removePost(post.id)
            }

            override fun onPostItemClick(post: Post) {
                viewModel.editContent(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_postReview,
                    Bundle().apply {
                        idPost = post.id.toString()
                        author = post.author
                        published = post.published
                        content = post.content
                    })
            }

            override fun onRetrySendPost(post: Post) {
                viewModel.retrySendPost(post)
            }

            override fun onClickImage(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageViewFragment,
                    Bundle().apply {
                        urlImage = post.attachment?.url
                    }
                )
            }

            override fun onEdit(post: Post) {
                viewModel.editContent(post)
                findNavController().navigate(R.id.action_feedFragment_to_editPost,
                    Bundle().apply {
                        authorEdit = post.author
                        publishedEdit = post.published
                        contentEdit = post.content
                    })
            }
        })
        swipeRefreshLayout.setOnRefreshListener(adapter::refresh)
        fab.setOnClickListener {
            if (viewModel.checkSignIn()) {
                findNavController().navigate(R.id.action_feedFragment_to_addNewPost)
            } else {
                dialogSign()
            }
        }
        rvPostList.adapter = adapter
        lifecycleScope.launchWhenCreated {
            viewModel.posts.collectLatest(adapter::submitData)
        }
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                swipeRefreshLayout.isRefreshing =
                    state.refresh is LoadState.Loading ||
                            state.prepend is LoadState.Loading ||
                            state.append is LoadState.Loading
            }
        }
        viewModel.state.observe(viewLifecycleOwner) { model ->
            groupStatus.isVisible = model.errorVisible
            tvTextStatusEmpty.isVisible = model.empty
            tvTextStatusError.text = model.error?.code
            pbProgress.isVisible = model.loading
            swipeRefreshLayout.isRefreshing = model.refreshing
            fabExtended.isVisible = model.visibleFab
        }
        errorButton.setOnClickListener {
            viewModel.loadPosts()
        }
    }.root

    private fun dialogSign() {
        AlertDialog.Builder(view?.context)
            .setTitle(R.string.sign_in)
            .setMessage(R.string.message_add_dialog)
            .setPositiveButton(R.string.dialog_btn_yes) { _, _ ->
                findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
            }
            .setNegativeButton(R.string.dialog_btn_no) { _, _ ->
                return@setNegativeButton
            }
            .show()
    }
}