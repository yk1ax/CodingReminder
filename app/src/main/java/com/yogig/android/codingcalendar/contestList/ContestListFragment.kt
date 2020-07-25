package com.yogig.android.codingcalendar.contestList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yogig.android.codingcalendar.ContestListAdapter
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

        viewModel = ViewModelProvider(this).get(ContestListViewModel::class.java)
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

        return binding.root
    }


}