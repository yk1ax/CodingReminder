package com.yogig.android.codingcalendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        viewModel = ViewModelProvider(this).get(NewContestViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.calendarSetEvent.observe(viewLifecycleOwner, Observer {
            if(it == 1) {
                binding.startDate.setDate()
                viewModel.onCalendarSetEventComplete()
            } else if(it == 2) {
                binding.endDate.setDate()
                viewModel.onCalendarSetEventComplete()
            }
        })

        viewModel.timeSetEvent.observe(viewLifecycleOwner, Observer {
            if(it == 1) {
                binding.startTime.setTime()
                viewModel.onTimeSetEventComplete()
            } else if(it == 2) {
                binding.endTime.setTime()
                viewModel.onTimeSetEventComplete()
            }
        })

        return binding.root
    }

    private fun TextView.setDate() {
        val calendar = Calendar.getInstance()
        val mYear = calendar.get(Calendar.YEAR)
        val mMonth = calendar.get(Calendar.MONTH)
        val mDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(this.context, DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            setText(
                "$dayOfMonth ${calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH)}, $year"
            )
        }, mYear, mMonth, mDay)
        dialog.show()
    }

    private fun TextView.setTime() {
        val calendar = Calendar.getInstance()
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)
        val dialog = TimePickerDialog(this.context, TimePickerDialog.OnTimeSetListener {
                view, hourOfDay, minute ->
            setText(
                "$hourOfDay:$minute"
            )
        }, mHour, mMinute, true)
        dialog.show()
    }
}