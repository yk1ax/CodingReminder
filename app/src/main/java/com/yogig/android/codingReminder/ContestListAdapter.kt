package com.yogig.android.codingReminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yogig.android.codingReminder.databinding.RecyclerViewItemBinding
import com.yogig.android.codingReminder.repository.Contest

class ContestListAdapter(val onClickListener: OnClickListener):
    ListAdapter<Contest, ContestListAdapter.ContestViewHolder>(DiffCallback) {

    class ContestViewHolder(private var binding: RecyclerViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contest: Contest) {
            binding.contestProperty = contest
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Contest>() {
        override fun areItemsTheSame(oldItem: Contest, newItem: Contest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contest, newItem: Contest): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        return ContestViewHolder(RecyclerViewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        val conetestProperty = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(conetestProperty)
        }

        holder.bind(conetestProperty)
    }

    class OnClickListener(private val clickListener: (contest: Contest) -> Unit) {
        fun onClick(contest: Contest) = clickListener(contest)
    }
}