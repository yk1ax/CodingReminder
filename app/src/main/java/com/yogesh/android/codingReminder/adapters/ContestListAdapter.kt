package com.yogesh.android.codingReminder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yogesh.android.codingReminder.databinding.RecyclerViewItemBinding
import com.yogesh.android.codingReminder.repository.Contest

/**
 * Adapter for the recyclerview used to display the contest list
 */
class ContestListAdapter(val onClickListener: OnClickListener):
    ListAdapter<Contest, ContestListAdapter.ContestViewHolder>(
        DiffCallback
    ) {

    class ContestViewHolder(private var binding: RecyclerViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contest: Contest) {
            binding.contestProperty = contest
            binding.executePendingBindings()
        }
    }

    /**
     * DiffCallBack being used for determining which items items in the recyclerview have
     * changed and thus only updating them and for determining new items which are to be added
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Contest>() {
        override fun areItemsTheSame(oldItem: Contest, newItem: Contest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contest, newItem: Contest): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestViewHolder {
        return ContestViewHolder(
            RecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ContestViewHolder, position: Int) {
        val contestProperty = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(contestProperty)
        }

        holder.bind(contestProperty)
    }

    /**
     * Class used for passing the onClickListener for contestEvent
     */
    class OnClickListener(private val clickListener: (contest: Contest) -> Unit) {
        fun onClick(contest: Contest) = clickListener(contest)
    }
}