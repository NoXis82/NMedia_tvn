package ru.netology.nmedia.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostCardBinding
import ru.netology.nmedia.dto.*

class PostsAdapter(
    private val onInteractionListener: IOnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val postView = PostCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(postView, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: PostCardBinding,
    private val onInteractionListener: IOnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
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

            menuPost.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_menu_post)
                    setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.postRemove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.postEdit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            likes.setOnClickListener {
                onInteractionListener.onLike(post)

            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
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