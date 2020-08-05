package com.yogig.android.codingReminder.contestList

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.yogig.android.codingReminder.ContestListAdapter
import com.yogig.android.codingReminder.R
import com.yogig.android.codingReminder.database.ContestDatabase
import com.yogig.android.codingReminder.databinding.ContestListFragmentBinding

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
    ): View? {
        binding = ContestListFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val application = requireNotNull(activity).application
        val database = ContestDatabase.getInstance(application)

        val viewModelFactory = ContestListViewModelFactory(database,application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ContestListViewModel::class.java)
        binding.viewModel = viewModel

        val columnCount = resources.getInteger(R.integer.grid_column_count)
        binding.contestRecyclerView.layoutManager = GridLayoutManager(context, columnCount)
        binding.contestRecyclerView.adapter = ContestListAdapter(ContestListAdapter.OnClickListener {
            viewModel.onCalendarNavigate(it)
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.retryFetching()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.progressBarVisible.observe(viewLifecycleOwner, Observer {
            if(it == 1) {
                binding.progressIndicator.visibility = View.VISIBLE
            }
            else if(it == 0) {
                binding.progressIndicator.visibility = View.GONE
            }
        })

        // Work on it for different types of snackBars depending upon the type of result
        viewModel.snackBarText.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it.startsWith("Fetched") || it == getString(R.string.no_new_contest)) {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()
                } else {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setAction("RETRY") { viewModel.retryFetching() }
                        .show()
                }
                viewModel.onCompleteSnackBarEvent()
            }
        })

        viewModel.calendarEvent.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                this.findNavController().navigate(ContestListFragmentDirections
                    .actionContestListFragmentToContestFragment(it))
                viewModel.onCalendarNavigateCompleted()
            }
        })

        viewModel.newContestEvent.observe(viewLifecycleOwner, Observer {
            if(it) {
                this.findNavController().navigate(ContestListFragmentDirections
                    .actionContestListFragmentToNewContest())
                viewModel.onNewContestNavigateComplete()
            }
        })

        viewModel.currentContestList.observe(viewLifecycleOwner, Observer {
            if(it.isNullOrEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.emptyView.visibility = View.GONE
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.refresh_item -> {
                viewModel.retryFetching()
                true
            }
            else -> {
                false
            }
        }
    }
}