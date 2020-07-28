package com.yogig.android.codingcalendar.contestList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.yogig.android.codingcalendar.ContestListAdapter
import com.yogig.android.codingcalendar.R
import com.yogig.android.codingcalendar.database.ContestDatabase
import com.yogig.android.codingcalendar.databinding.ContestListFragmentBinding

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

        binding.contestRecyclerView.adapter = ContestListAdapter()

        viewModel.progressBarVisible.observe(viewLifecycleOwner, Observer {
            if(it) {
                binding.progressIndicator.visibility = View.VISIBLE
            }
            else {
                binding.progressIndicator.visibility = View.GONE
            }
        })

        viewModel.snackBarText.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it.substring(0..6) == "Fetched") {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                } else if(it == getString(R.string.no_internet)){
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", View.OnClickListener { viewModel.retryFetching() })
                        .show()
                }
                viewModel.onCompleteSnackBarEvent()
            }
        })

        return binding.root
    }


}