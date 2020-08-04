package com.yogig.android.codingcalendar.contestFragment

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yogig.android.codingcalendar.R
import com.yogig.android.codingcalendar.database.ContestDatabase
import com.yogig.android.codingcalendar.database.DatabaseContest
import com.yogig.android.codingcalendar.databinding.ContestFragmentBinding
import com.yogig.android.codingcalendar.repository.Contest
import java.util.*

class ContestFragment : Fragment() {

    companion object {
        fun newInstance() =
            ContestFragment()
    }

    lateinit var viewModel: ContestViewModel
    lateinit var contest: Contest
    lateinit var binding: ContestFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Obtain the data binding object
        binding =  ContestFragmentBinding.inflate(inflater, container, false)

        // Obtain the PackageManager for checking the existence of support for the intents
        val packageManager = requireNotNull(context).packageManager
        binding.lifecycleOwner = this

        // Creating the ViewModel
        val database = ContestDatabase.getInstance(requireContext())
        val viewModelFactory = ContestViewModelFactory(database)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ContestViewModel::class.java)

        // Extracting the Contest from the FragmentArgs passed in by safe-args
        contest = ContestFragmentArgs.fromBundle(requireArguments()).contest

        // Binding the Contest Property to facilitate it's display
        binding.contestProperty = contest

        // Linking the ContestViewModel object with the viewModel variable present in the XML
        binding.viewModel = viewModel

        // Disabling the Add event buttons in case the contest has started
        val curTime = System.currentTimeMillis()
        if(contest.startTimeMilliseconds <= curTime) {
            binding.calendarButton.isEnabled = false
        }
        if(contest.websiteUrl.isNullOrEmpty()) {
            binding.websiteButton.isEnabled = false
        }

        // Calendar Intent for sending the event to the Calendar App
        val calendarIntent = Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, contest.name)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, contest.startTimeMilliseconds)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, contest.endTimeSeconds)
            .putExtra(CalendarContract.Events.DESCRIPTION, contest.websiteUrl)
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)

        // Website Intent to open the website using the website URL
        val websiteIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(contest.websiteUrl))

        // Observe the calendarEvent boolean variable of the viewModel for
        // initiating the Calendar event
        viewModel.calendarEvent.observe(viewLifecycleOwner, Observer {
            if(it) {
                val activities = packageManager.queryIntentActivities(
                    calendarIntent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

                if(activities.isNotEmpty()) {
                    startActivity(calendarIntent)
                } else {
                    Snackbar.make(binding.root, "Calendar app not found.", Snackbar.LENGTH_LONG)
                }

                viewModel.onCalendarEventComplete()
            }
        })

        // Observe the websiteEvent boolean variable of the viewModel for
        // initiating the Website Intent
        viewModel.websiteEvent.observe(viewLifecycleOwner, Observer {
            if(it) {
                val activities = packageManager.queryIntentActivities(
                    websiteIntent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

                if(activities.isNotEmpty()) {
                    startActivity(websiteIntent)
                } else {
                    Snackbar.make(binding.root, "Internet browser not found.", Snackbar.LENGTH_LONG)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .show()
                }

                viewModel.onWebsiteEventComplete()
            }
        })

        setHasOptionsMenu(true)
        // Return the root view of the binding, i.e., the fragment itself
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contest_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.delete_item -> {
                viewModel.onContestDelete(DatabaseContest(
                    contest.id,
                    contest.name,
                    contest.startTimeMilliseconds,
                    contest.endTimeSeconds,
                    contest.site,
                    contest.websiteUrl
                ))
                Snackbar.make(binding.root, "Contest deleted.", Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .show()
                this.findNavController().navigateUp()
                true
            }
            else -> {
                false
            }
        }
    }

}