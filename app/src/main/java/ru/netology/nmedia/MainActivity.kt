package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import ru.netology.nmedia.dto.*


class MainActivity : AppCompatActivity() {
    private val posts = mutableListOf<Post>()
    private var shareValue = 0
    private var likesValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createPostTest()
        actionClickBtnLike()
        actionClickBtnShare()
    }

    private fun actionClickBtnShare() {
        shareValue = posts[0].share
        share?.setOnClickListener {
            shareValue++
            posts[0] = posts[0].copy(share = shareValue)
            shareCount.text = formatCountToStr(posts[0].share)
        }
    }

    private fun actionClickBtnLike() {
        likes?.setOnClickListener {
            likesValue = posts[0].likes
            if (posts[0].likedByMe) {
                likesValue--
                likes?.setImageResource(R.drawable.ic_thumb_up_24)
                posts[0] = posts[0].copy(likes = likesValue, likedByMe = false)
            } else {
                likesValue++
                likes?.setImageResource(R.drawable.ic_thumb_true_up_24)
                posts[0] = posts[0].copy(likes = likesValue, likedByMe = true)
            }
            likesCount.text = formatCountToStr(posts[0].likes)
        }
    }

    private fun formatCountToStr(value: Int): String {
        return when(value/1000) {
            0 -> "$value"
            in 1..9 -> {
                val str = "%.1f".format(value/1000.0)
                "${str}K"
            }
            in 10..999 -> {
                val res = value/1000
                "${res}K"
            }
            else -> {
                val str = "%.1f".format(value/1000000.0)
                "${str}М"
            }
        }
    }

    private fun createPostTest() {
        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            share = 12345678,
            likes = 79,
            views = 5867
        )
        posts.add(post)
        author.text = post.author
        published.text = post.published
        content.text = post.content
        likesCount.text = formatCountToStr(post.likes)
        shareCount.text = formatCountToStr(post.share)
        chatCount.text = formatCountToStr(post.chat)
        viewCount.text = formatCountToStr(post.views)
    }


}