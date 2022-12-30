package com.yogesh.android.codingReminder.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yogesh.android.codingReminder.R
import com.yogesh.android.codingReminder.viewModels.ContestViewModel
import com.yogesh.android.codingReminder.viewModels.ContestViewModelFactory
import com.yogesh.android.codingReminder.createChannel
import com.yogesh.android.codingReminder.database.ContestDatabase
import com.yogesh.android.codingReminder.databinding.ContestFragmentBinding
import com.yogesh.android.codingReminder.repository.Contest

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
    ): View {
        // Extracting the Contest from the FragmentArgs passed in by safe-args
        contest = ContestFragmentArgs.fromBundle(requireArguments()).contest

        binding =  ContestFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val database = ContestDatabase.getInstance(requireContext())
        val application = requireActivity().application
        val viewModelFactory = ContestViewModelFactory(application, database, contest)
        viewModel = ViewModelProvider(this, viewModelFactory)[ContestViewModel::class.java]
        // Linking the ContestViewModel object with the viewModel variable present in the XML
        binding.viewModel = viewModel

        // Binding the Contest Property to facilitate it's display
        binding.contestProperty = contest

        Log.i("ContestFragment","contest id = ${contest.id}")

        // Obtain the PackageManager for checking the existence of activities for the implicit intents
        val packageManager = requireNotNull(context).packageManager

        // Observe the calendarEvent boolean variable of the viewModel for
        // initiating the Calendar event
        viewModel.calendarEvent.observe(viewLifecycleOwner) {
            if (it) {
                calendarEventHandler(contest)
            }
        }

        // Observe the websiteEvent boolean variable of the viewModel for
        // initiating the Website Intent
        viewModel.websiteEvent.observe(viewLifecycleOwner) {
            if (it) {
                websiteEventHandler(contest)
            }
        }

        viewModel.notificationEvent.observe(viewLifecycleOwner) {
            if (it) {
                notificationEventHandler()
            }
        }

        createChannel(getString(R.string.contest_notification_channel_id), getString(R.string.contest_notification_channel_name), requireActivity())

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
                viewModel.deleteContest()
                viewModel.removeNotification()
                Snackbar.make(binding.root, "Contest deleted.", Snackbar.LENGTH_LONG)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .show()

                this.findNavController().navigateUp()
                true
            }
            else -> false
        }
    }

    private fun calendarEventHandler(contest: Contest) {
        // Calendar Intent for sending the event to the Calendar App
        val calendarIntent = Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, contest.name)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, contest.startTimeMilliseconds)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, contest.endTimeSeconds)
            .putExtra(CalendarContract.Events.DESCRIPTION, contest.websiteUrl)
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        try {
            startActivity(calendarIntent)
        } catch (e : ActivityNotFoundException) {
            Snackbar
                .make(binding.root, "Calendar app not found.", Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .show()
        }
        viewModel.onCalendarEventComplete()
    }

    private fun websiteEventHandler(contest: Contest) {
        try {
            // Website Intent to open the website using the website URL
            val websiteIntent = Intent(Intent.ACTION_VIEW, Uri.parse(contest.websiteUrl))
            startActivity(websiteIntent)
        } catch (e : ActivityNotFoundException) {
            Snackbar.make(binding.root, "Internet browser not found.", Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .show()
        }
        viewModel.onWebsiteEventComplete()
    }

    private fun notificationEventHandler() {
        if (viewModel.notificationAlreadySet.value!!) {
            viewModel.removeNotification()
            Snackbar
                .make(binding.root, getString(R.string.reminder_removed), Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .show()
        } else {
            viewModel.setNotification()
            Snackbar
                .make(binding.root, getString(R.string.notification_set), Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .show()
        }
        viewModel.onNotificationEventComplete()
    }
}