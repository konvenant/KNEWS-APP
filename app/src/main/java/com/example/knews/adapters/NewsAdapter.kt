package com.example.knews.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.knews.R
import com.example.knews.models.Article


class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val  differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
          val article = differ.currentList[position]
        holder.itemView.apply {
            val ivArticleImage = findViewById<ImageView>(R.id.ivArticleImage)
            val tvSource = findViewById<TextView>(R.id.tvSource)
            val tvTitle = findViewById<TextView>(R.id.tvTitle)
            val tvDescription = findViewById<TextView>(R.id.tvDescription)
            val tvPublishedAt = findViewById<TextView>(R.id.tvPublishedAt)
            val progressBar = findViewById<ProgressBar>(R.id.imageProgressBar)
            Glide.with(this).load(article.urlToImage)
                .apply(RequestOptions.placeholderOf(R.drawable.logo))
                .listener(GlideImageLoadingListener(progressBar))
                .into(ivArticleImage)

            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
       return ArticleViewHolder(
           LayoutInflater.from(parent.context).inflate(
               R.layout.item_article_preview,
               parent,
               false
           )
       )
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }


    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }







}