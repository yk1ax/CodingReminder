package com.yogig.android.codingcalendar.contestFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yogig.android.codingcalendar.databinding.ContestFragmentBinding

class ContestFragment : Fragment() {

    companion object {
        fun newInstance() =
            ContestFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =  ContestFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val viewModel = ViewModelProvider(this).get(ContestViewModel::class.java)
        val contest = ContestFragmentArgs.fromBundle(arguments!!).contest
        binding.contestProperty = contest
        binding.viewModel = viewModel

        val curTime = System.currentTimeMillis()
        if(contest.startTimeMilliseconds <= curTime) {
            binding.calendarButton.isEnabled = false
        }

        val calendarIntent = Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, contest.name)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, contest.startTimeMilliseconds)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, contest.endTimeSeconds)
            .putExtra(CalendarContract.Events.DESCRIPTION, contest.websiteUrl)
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)

        val websiteIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(contest.websiteUrl))

        viewModel.calendarEvent.observe(viewLifecycleOwner, Observer {
            if(it) {
                startActivity(calendarIntent)
                viewModel.onCalendarEventComplete()
            }
        })

        viewModel.websiteEvent.observe(viewLifecycleOwner, Observer {
            if(it) {
                startActivity(websiteIntent)
                viewModel.onWebsiteEventComplete()
            }
        })

        return binding.root
    }

}