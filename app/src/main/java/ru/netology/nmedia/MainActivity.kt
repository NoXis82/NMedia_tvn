package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.observe
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likesCount.text = formatCountToStr(post.likes)
                shareCount.text = formatCountToStr(post.share)
                chatCount.text = formatCountToStr(post.chat)
                viewCount.text = formatCountToStr(post.views)
                likes.setImageResource(
                    if (post.likedByMe) R.drawable.ic_thumb_true_up_24
                    else R.drawable.ic_thumb_up_24
                )
            }
        }
        binding.likes.setOnClickListener {
            viewModel.like()
        }

        binding.share.setOnClickListener {
            viewModel.share()
        }

    }

    private fun formatCountToStr(value: Int): String {
        return when (value / 1000) {
            0 -> "$value"
            in 1..9 -> {
                val str = "%.1f".format(value / 1000.0)
                    .dropLastWhile { it == '0' }
                    .dropLastWhile { it == '.' }
                "${str}K"
            }
            in 10..999 -> {
                val res = value / 1000
                "${res}K"
            }
            else -> {
                val str = "%.1f".format(value / 1000000.0)
                    .dropLastWhile { it == '0' }
                    .dropLastWhile { it == '.' }
                "${str}М"
            }
        }
    }

}