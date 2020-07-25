package com.yogig.android.codingcalendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yogig.android.codingcalendar.databinding.RecyclerViewItemBinding
import com.yogig.android.codingcalendar.network.NetworkContest

class ContestListAdapter:
    ListAdapter<NetworkContest, ContestListAdapter.ContestViewHolder>(DiffCallback) {

    class ContestViewHolder(private var binding: RecyclerViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contest: NetworkContest) {
            binding.contestProperty = contest
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<NetworkContest>() {
        override fun areItemsTheSame(oldItem: NetworkContest, newItem: NetworkContest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NetworkContest, newItem: NetworkContest): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        return ContestViewHolder(RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        val conetestProperty = getItem(position)

        holder.bind(conetestProperty)
    }
}