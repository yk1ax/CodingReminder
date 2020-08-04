package com.yogig.android.codingcalendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yogig.android.codingcalendar.database.ContestDatabase
import com.yogig.android.codingcalendar.databinding.NewContestFragmentBinding
import java.util.*

class NewContestFragment : Fragment() {

    companion object {
        fun newInstance() = NewContestFragment()
    }

    private lateinit var viewModel: NewContestViewModel
    private lateinit var binding: NewContestFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NewContestFragmentBinding.inflate(inflater)
        val database = ContestDatabase.getInstance(requireContext().applicationContext)
        val application = requireActivity().application
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
            if(it != null) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .show()
            }
        })

        viewModel.submitEvent.observe(viewLifecycleOwner, Observer {
            if(it) {
                if(isTimeRangeSet()) {
                    viewModel.trySubmit()
                    viewModel.onSubmitEventComplete()
                    this.findNavController().navigateUp()
                } else {
                    Snackbar.make(binding.root, "Set the time range.", Snackbar.LENGTH_LONG)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .show()
                }
            }
        })

        val focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!hasFocus) {
                    val inputMethodManager = requireContext().getSystemService(
                        InputMethodManager::class.java
                    ) as InputMethodManager

                    inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
                } else {
                    val inputMethodManager = requireContext().getSystemService(
                        InputMethodManager::class.java
                    ) as InputMethodManager

                    inputMethodManager.showSoftInput(v, 0)
                }
            }
        }

        binding.contestNameEditText.setOnFocusChangeListener(focusChangeListener)
        binding.contestLinkEditText.setOnFocusChangeListener(focusChangeListener)



        return binding.root
    }

    private fun isTimeRangeSet(): Boolean {
        return when {
            binding.startDate.text == getString(R.string.start_date) -> false
            binding.startTime.text == getString(R.string.start_time) -> false
            binding.endDate.text == getString(R.string.end_date) -> false
            binding.endTime.text == getString(R.string.end_time) -> false
            else -> true
        }
    }
}