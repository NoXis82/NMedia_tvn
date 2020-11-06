package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.post_card.view.*
import ru.netology.nmedia.adapter.IOnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(object : IOnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.like(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.share(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removePost(post.id)
           }

            override fun onEdit(post: Post) {
                viewModel.editContent(post)
            }

        })

        binding.rvPostList.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }
            with(binding.editNewContent) {
                binding.groupCancelEdit.visibility = View.VISIBLE
                binding.authorEdit.text = post.author
                requestFocus()
                setText(post.content)
            }
        }

        binding.btnCancelEdit.setOnClickListener {
            with(binding.editNewContent) {
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.groupCancelEdit.visibility = View.GONE
            }
        }

        binding.btnSavePost.setOnClickListener {
            with(binding.editNewContent) {
                if(TextUtils.isEmpty(text)) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_post),
                        Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }
                viewModel.changeContent(text.toString())
                viewModel.savePost()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
                binding.groupCancelEdit.visibility = View.GONE
            }
        }

    }
}