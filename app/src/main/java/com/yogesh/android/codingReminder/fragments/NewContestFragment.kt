package com.yogesh.android.codingReminder.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yogesh.android.codingReminder.*
import com.yogesh.android.codingReminder.adapters.setDate
import com.yogesh.android.codingReminder.adapters.setTime
import com.yogesh.android.codingReminder.database.ContestDatabase
import com.yogesh.android.codingReminder.databinding.NewContestFragmentBinding
import com.yogesh.android.codingReminder.viewModels.NewContestViewModel
import com.yogesh.android.codingReminder.viewModels.NewContestViewModelFactory

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
    ): View {
        binding = NewContestFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val application = requireActivity().application
        val database = ContestDatabase.getInstance(requireContext().applicationContext)

        val viewModelFactory = NewContestViewModelFactory(database, application)
        viewModel = ViewModelProvider(this, viewModelFactory)[NewContestViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.calendarSetEvent.observe(viewLifecycleOwner) {
            calendarSetEventHandler(it)
        }

        viewModel.timeSetEvent.observe(viewLifecycleOwner) {
            timeSetEventHandler(it)
        }

        viewModel.snackBarText.observe(viewLifecycleOwner) {
            it?.let {
                snackBarHandler(it)
            }
        }

        viewModel.submitEvent.observe(viewLifecycleOwner) {
            if (it) {
                submitHandler()
            }
        }

        val focusChangeListener = getFocusChangeListener(requireContext())
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

    private fun calendarSetEventHandler(type: Int) {
        when (type) {
            viewModel.START -> {
                binding.startDate.setDate(type)
                viewModel.onTimeSetEventComplete()
            }
            viewModel.END -> {
                binding.endDate.setDate(type)
                viewModel.onTimeSetEventComplete()
            }
        }
    }

    private fun timeSetEventHandler(type: Int) {
        when (type) {
            viewModel.START -> {
                binding.startTime.setTime(type)
                viewModel.onTimeSetEventComplete()
            }
            viewModel.END -> {
                binding.endTime.setTime(type)
                viewModel.onTimeSetEventComplete()
            }
        }
    }

    private fun snackBarHandler(text: String) {
        Snackbar
            .make(binding.root, text, Snackbar.LENGTH_LONG)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }

    private fun submitHandler() {
        val isSubmitted = viewModel.trySubmit(isTimeRangeSet())
        if (isSubmitted) {
            viewModel.onSubmitEventComplete()
            this.findNavController().navigateUp()
        }
    }
}

fun getFocusChangeListener(context: Context): View.OnFocusChangeListener {
    return View.OnFocusChangeListener { v, hasFocus ->
        val inputMethodManager = ContextCompat.getSystemService(
            context,
            InputMethodManager::class.java
        ) as InputMethodManager

        if (!hasFocus) {
            inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
        } else {
            inputMethodManager.showSoftInput(v, 0)
        }
    }
}