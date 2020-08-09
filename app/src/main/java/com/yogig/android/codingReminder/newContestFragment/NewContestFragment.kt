package com.yogig.android.codingReminder.newContestFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yogig.android.codingReminder.*
import com.yogig.android.codingReminder.database.ContestDatabase
import com.yogig.android.codingReminder.databinding.NewContestFragmentBinding

class NewContestFragment : Fragment() {

    companion object {
        fun newInstance() =
            NewContestFragment()
    }

    private lateinit var viewModel: NewContestViewModel
    private lateinit var binding: NewContestFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NewContestFragmentBinding.inflate(inflater)

        val application = requireActivity().application
        val database = ContestDatabase.getInstance(requireContext().applicationContext)
        val viewModelFactory = NewContestViewModelFactory(database, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NewContestViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.calendarSetEvent.observe(viewLifecycleOwner, Observer {
            if (it == 1) {
                binding.startDate.setDate(it)
                viewModel.onCalendarSetEventComplete()
            } else if (it == 2) {
                binding.endDate.setDate(it)
                viewModel.onCalendarSetEventComplete()
            }
        })

        viewModel.timeSetEvent.observe(viewLifecycleOwner, Observer {
            if (it == 1) {
                binding.startTime.setTime(it)
                viewModel.onTimeSetEventComplete()
            } else if (it == 2) {
                binding.endTime.setTime(it)
                viewModel.onTimeSetEventComplete()
            }
        })

        viewModel.snackBarText.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .show()
            }
        })

        viewModel.submitEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                val isSubmitted = viewModel.trySubmit(isTimeRangeSet())
                if (isSubmitted) {
                    viewModel.onSubmitEventComplete()
                    this.findNavController().navigateUp()
                }
            }
        })

        val focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            val inputMethodManager = ContextCompat.getSystemService(
                requireContext(),
                InputMethodManager::class.java
            ) as InputMethodManager

            if (!hasFocus) {
                inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
            } else {
                inputMethodManager.showSoftInput(v, 0)
            }

        }

        binding.contestNameEditText.onFocusChangeListener = focusChangeListener
        binding.contestLinkEditText.onFocusChangeListener = focusChangeListener

        return binding.root
    }

    private fun isTimeRangeSet(): Boolean {
        return when {
            binding.startDate.text.toString() == getString(R.string.start_date) -> false
            binding.startTime.text.toString() == getString(R.string.start_time) -> false
            binding.endDate.text.toString() == getString(R.string.end_date) -> false
            binding.endTime.text.toString() == getString(R.string.end_time) -> false
            else -> true
        }
    }


}