package com.github.sample.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sample.GithubApplication
import com.github.sample.R
import com.github.sample.models.RepoItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_item_repo_details.view.*


class MainAdapter(val showPic: Boolean, recyclerView: RecyclerView) :
    RecyclerView.Adapter<MainAdapter.MyViewHolder>() {

    private var items: MutableList<RepoItem> = mutableListOf()
    private var loading: Boolean = false
    lateinit var onItemClickListener: OnItemClickListener
    lateinit var onLoadMoreListener: OnLoadMoreListener


    init {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val totalItemCount = linearLayoutManager.itemCount
                    val lastVisibleItem =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition()

                    if (!loading && totalItemCount - 1 <= lastVisibleItem && lastVisibleItem > items.size - 5) {
                        onLoadMoreListener.onLoadMore()
                        loading = true
                    }
                }
            })
        }
    }

    fun updateItems(items: List<RepoItem>) {
        items.forEach { this.items.add(it) }

        notifyDataSetChanged()
    }

    fun clearItems() {
        this.items.clear()
        Log.d("TAG", "ClearItems")
        notifyDataSetChanged()
    }

    fun setLoaded() {
        loading = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_item_repo_details,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindData(item: RepoItem) {
            var repoId = item.repoId.toString()
            itemView.textViewName.text = item.name
            itemView.textViewFullName.text = item.fullName
            var editor = GithubApplication.sharedPreferences.edit()
            var favEnabled = GithubApplication.sharedPreferences.getBoolean(repoId, false)
            itemView.favouriteButton.isChecked = favEnabled
            itemView.favouriteButton.setOnClickListener {
                if (itemView.favouriteButton.isChecked) {
                    editor.putBoolean(repoId, true).apply()
                } else {
                    if (GithubApplication.sharedPreferences.contains(repoId)) {
                        editor.remove(repoId).apply()
                    }
                }
            }
            if (showPic) {
                Picasso.get().load(item.owner?.avatarUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(itemView.imageViewProfile)
            } else {
                itemView.imageViewProfile.visibility = View.GONE
            }
        }

        override fun onClick(view: View?) {
            onItemClickListener.onItemClick(items[adapterPosition])
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item: RepoItem)
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

}
