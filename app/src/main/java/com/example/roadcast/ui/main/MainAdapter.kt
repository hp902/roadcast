package com.example.roadcast.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roadcast.R
import com.example.roadcast.databinding.EntryItemBinding

class MainAdapter : ListAdapter<Entries, MainAdapter.ViewHolder>(DiffUtilCallBack()) {

    inner class ViewHolder(val binding: EntryItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            EntryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        with(holder.binding) {
            tvApiName.apply {
                text = resources.getString(R.string.api_text, data.api)
            }
            tvDesc.apply {
                text = resources.getString(R.string.desc_text, data.description)
            }
            tvCategory.apply {
                text = resources.getString(R.string.category_text, data.category)
            }
            tvLink.apply {
                text = resources.getString(R.string.lint_text, data.link)
            }
        }
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<Entries>() {

        override fun areItemsTheSame(oldItem: Entries, newItem: Entries): Boolean {
            return oldItem.link == newItem.link
        }

        override fun areContentsTheSame(oldItem: Entries, newItem: Entries): Boolean {
            return oldItem == newItem
        }

    }

}