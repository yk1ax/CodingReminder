package com.yogesh.android.codingReminder.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yogesh.android.codingReminder.adapters.ContestListAdapter
import com.yogesh.android.codingReminder.R
import com.yogesh.android.codingReminder.viewModels.ContestListViewModel
import com.yogesh.android.codingReminder.viewModels.ContestListViewModelFactory
import com.yogesh.android.codingReminder.database.ContestDatabase
import com.yogesh.android.codingReminder.databinding.ContestListFragmentBinding
import com.yogesh.android.codingReminder.repository.Contest

class ContestListFragment : Fragment() {

    companion object {
        fun newInstance() =
            ContestListFragment()
    }

    private lateinit var viewModel: ContestListViewModel
    private lateinit var binding: ContestListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ContestListFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val application = requireNotNull(activity).application
        val database = ContestDatabase.getInstance(application)

        val viewModelFactory = ContestListViewModelFactory(database, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[ContestListViewModel::class.java]
        binding.viewModel = viewModel

        val columnCount = resources.getInteger(R.integer.grid_column_count)
        binding.contestRecyclerView.layoutManager = GridLayoutManager(context, columnCount)
        binding.contestRecyclerView.adapter = ContestListAdapter(
            ContestListAdapter.OnClickListener {  viewModel.onContestNavigate(it) }
        )

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.retryFetching() }

        viewModel.refreshingState.observe(viewLifecycleOwner) {
            refreshingHandler(it)
        }

        viewModel.snackBarText.observe(viewLifecycleOwner) {
            it?.let { snackBarHandler(it) }
        }

        viewModel.contestEvent.observe(viewLifecycleOwner) {
            it?.let { contestEventHandler(it) }
        }

        viewModel.newContestEvent.observe(viewLifecycleOwner) {
            if (it) {
                contestEventHandler()
            }
        }

        return binding.root
    }

    override fun onStart() {
        binding.shimmerLayout.showShimmer(true)
        super.onStart()
    }

    override fun onStop() {
        binding.shimmerLayout.showShimmer(false)
        super.onStop()
    }
    private fun refreshingHandler(isRefreshing: Boolean) {
        if (isRefreshing) {
            binding.progressIndicator.show()
            binding.contestRecyclerView.visibility = View.GONE
            binding.shimmerLayout.visibility = View.VISIBLE
            binding.shimmerLayout.showShimmer(true)
        } else {
            binding.progressIndicator.hide()
            binding.shimmerLayout.showShimmer(false)
            binding.shimmerLayout.visibility = View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
            if (viewModel.currentContestList.value?.size == 0) {
                binding.contestRecyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.contestRecyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
            }
        }
    }

    private fun snackBarHandler(text: String) {
        if (text.startsWith("Fetched") || text == getString(R.string.no_new_contest)) {
            Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setAnchorView(binding.fab)
                .show()
        } else {
            Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .setAnchorView(binding.fab)
                .setAction("RETRY") { viewModel.retryFetching() }
                .show()
        }
        viewModel.onCompleteSnackBarEvent()
    }

    private fun contestEventHandler() {
        this.findNavController()
            .navigate(ContestListFragmentDirections.actionContestListFragmentToNewContest())
        viewModel.onNewContestNavigateComplete()
    }

    private fun contestEventHandler(contest: Contest) {
        this.findNavController().navigate(
            ContestListFragmentDirections.actionContestListFragmentToContestFragment(contest)
        )
        viewModel.onContestNavigateComplete()
    }
}


