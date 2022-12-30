package com.yogesh.android.codingReminder.fragments

import android.content.Context
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
        binding.lifecycleOwner = this

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
            handleRefreshing(it, binding, viewModel)
        }

        viewModel.snackBarText.observe(viewLifecycleOwner) {
            it?.let { context?.let { it1 -> handleSnackBar(it, it1, binding, viewModel) } }
        }

        viewModel.contestEvent.observe(viewLifecycleOwner) {
            if (it !== null) {
                this.findNavController().navigate(
                    ContestListFragmentDirections.actionContestListFragmentToContestFragment(it)
                )
                viewModel.onContestNavigateComplete()
            }
        }

        viewModel.newContestEvent.observe(viewLifecycleOwner) {
            if (it) {
                this.findNavController()
                    .navigate(ContestListFragmentDirections.actionContestListFragmentToNewContest())
                viewModel.onNewContestNavigateComplete()
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
}

fun handleRefreshing(
    isRefreshing: Boolean,
    binding: ContestListFragmentBinding,
    viewModel: ContestListViewModel) {

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

fun handleSnackBar(
    text: String,
    context: Context,
    binding: ContestListFragmentBinding,
    viewModel: ContestListViewModel) {

    if (text.startsWith("Fetched") || text == context.getString(R.string.no_new_contest)) {
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