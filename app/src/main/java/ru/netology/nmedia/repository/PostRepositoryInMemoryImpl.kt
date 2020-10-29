package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.*

class PostRepositoryInMemoryImpl: PostRepository {
    private var likesValue = 0
    private var shareValue = 0
    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = "21 мая в 18:36",
        likes = 999,
        views = 5867
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        likesValue = post.likes
        post = if (post.likedByMe) {
            likesValue--
            post.copy(likes = likesValue, likedByMe = false)
        } else {
            likesValue++
            post.copy(likes = likesValue, likedByMe = true)
        }
        data.value = post
    }

    override fun share() {
        shareValue = post.share
        shareValue++
        post = post.copy(share = shareValue)
        data.value = post
    }

}