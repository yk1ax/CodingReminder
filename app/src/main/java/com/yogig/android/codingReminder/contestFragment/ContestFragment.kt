package com.yogig.android.codingReminder.contestFragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yogig.android.codingReminder.R
import com.yogig.android.codingReminder.database.ContestDatabase
import com.yogig.android.codingReminder.database.DatabaseContest
import com.yogig.android.codingReminder.databinding.ContestFragmentBinding
import com.yogig.android.codingReminder.repository.Contest

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

        // Extracting the Contest from the FragmentArgs passed in by safe-args
        contest = ContestFragmentArgs.fromBundle(requireArguments()).contest

        // Obtain the PackageManager for checking the existence of support for the intents
        val packageManager = requireNotNull(context).packageManager
        binding.lifecycleOwner = this

        // Creating the ViewModel
        val database = ContestDatabase.getInstance(requireContext())
        val application = requireActivity().application
        val viewModelFactory = ContestViewModelFactory(application, database, contest)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ContestViewModel::class.java)

        // Binding the Contest Property to facilitate it's display
        binding.contestProperty = contest

        // Linking the ContestViewModel object with the viewModel variable present in the XML
        binding.viewModel = viewModel

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

        viewModel.notificationEvent.observe(viewLifecycleOwner, Observer {
            if(it) {
                if(viewModel.notificationAlreadySet.value!!) {
                    viewModel.removeNotification(contest)
                    Snackbar.make(binding.root, getString(R.string.reminder_removed), Snackbar.LENGTH_LONG)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .show()
                } else {
                    viewModel.setNotification(contest)
                    Snackbar.make(binding.root, getString(R.string.notification_set), Snackbar.LENGTH_LONG)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .show()
                }

                viewModel.onNotificationEventComplete()
            }
        })

        createChannel(getString(R.string.contest_notification_channel_id), getString(R.string.contest_notification_channel_name))

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
                    contest.websiteUrl,
                    contest.isNotificationSet
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

    private fun createChannel(channelId: String, channelName: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply { setShowBadge(true) }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.WHITE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for contest"

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}