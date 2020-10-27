package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.*


class MainActivity : AppCompatActivity() {
    private val posts = mutableListOf<Post>()
    private var shareValue = 0
    private var likesValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            share = 12045678,
            likes = 999,
            views = 5867
        )
        posts.add(post)
        binding.author.text = post.author
        binding.published.text = post.published
        binding.content.text = post.content
        binding.likesCount.text = formatCountToStr(post.likes)
        binding.shareCount.text = formatCountToStr(post.share)
        binding.chatCount.text = formatCountToStr(post.chat)
        binding.viewCount.text = formatCountToStr(post.views)

        binding.likes.setOnClickListener {
            likesValue = posts[0].likes
            if (posts[0].likedByMe) {
                likesValue--
                binding.likes.setImageResource(R.drawable.ic_thumb_up_24)
                posts[0] = posts[0].copy(likes = likesValue, likedByMe = false)
            } else {
                likesValue++
                binding.likes.setImageResource(R.drawable.ic_thumb_true_up_24)
                posts[0] = posts[0].copy(likes = likesValue, likedByMe = true)
            }
            binding.likesCount.text = formatCountToStr(posts[0].likes)
        }

        binding.share.setOnClickListener {
            shareValue = posts[0].share
            shareValue++
            posts[0] = posts[0].copy(share = shareValue)
            binding.shareCount.text = formatCountToStr(posts[0].share)
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