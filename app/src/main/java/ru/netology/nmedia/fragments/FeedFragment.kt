package ru.netology.nmedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
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
import ru.netology.nmedia.model.getCreateReadableMessageError
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var removeId = 0L
        var checkPost = Post()
        val binding = FragmentFeedBinding.inflate(layoutInflater)
        val adapter = PostsAdapter(object : IOnInteractionListener {

            override fun onLike(post: Post) {
                checkPost = post
                viewModel.like(post)
            }

            override fun onShare(post: Post) {
                checkPost = post
                viewModel.sharePost(post)
            }

            override fun onRemove(post: Post) {
                removeId = post.id
                viewModel.removePost(post.id)
            }

            override fun playVideoPost(post: Post) {
                //       val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                //       startActivity(videoIntent)
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
                        // videoUrl = post.videoUrl
                    })
            }

            override fun onRetrySendPost(post: Post) {
                  viewModel.retrySendPost(post)
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
        binding.swipeRefreshLayout.setOnRefreshListener(viewModel::refreshingPosts)
        viewModel.postsRefreshError.observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, R.string.message_status_error, Snackbar.LENGTH_LONG)
                .setAction("Retry") { viewModel.refreshingPosts() }
                .show()
        }

        viewModel.postRemoveError.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                R.string.message_status_error,
                Snackbar.LENGTH_LONG
            )
                .setAction("Retry") { viewModel.removePost(removeId) }
                .show()
        }

        viewModel.postLikeError.observe(viewLifecycleOwner) {
            Snackbar.make(
                binding.root,
                R.string.message_status_error,
                Snackbar.LENGTH_SHORT
            )
                .setAction("Retry") { viewModel.like(checkPost) }
                .show()
        }


        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_addNewPost)
        }
        binding.rvPostList.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { model ->
            binding.groupStatus.isVisible = model.errorVisible
            binding.tvTextStatusEmpty.isVisible = model.empty
            binding.tvTextStatusError.text = model.error.getCreateReadableMessageError(resources)
            binding.pbProgress.isVisible = model.loading
            binding.swipeRefreshLayout.isRefreshing = model.refreshing
        }
        viewModel.posts.observe(viewLifecycleOwner, adapter::submitList)
        binding.errorButton.setOnClickListener {
            viewModel.loadPosts()
        }
        return binding.root
    }
}