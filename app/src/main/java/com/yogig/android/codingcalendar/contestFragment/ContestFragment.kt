package com.yogig.android.codingcalendar.contestFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yogig.android.codingcalendar.R
import com.yogig.android.codingcalendar.databinding.ContestFragmentBinding

class ContestFragment : Fragment() {

    companion object {
        fun newInstance() =
            ContestFragment()
    }

    private lateinit var viewModel: ContestViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =  ContestFragmentBinding.inflate(inflater, container, false)
        val viewModel = ViewModelProvider(this).get(ContestViewModel::class.java)
        binding.lifecycleOwner = this
        binding.contestProperty = ContestFragmentArgs.fromBundle(arguments!!).contest
        binding.viewModel = viewModel



        return binding.root
    }

}