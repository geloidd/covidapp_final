package com.bitioncompany.covid.ui.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitioncompany.covid.R
import com.bitioncompany.covid.database.EventModel
import com.bitioncompany.covid.utils.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.Query
import kotlinx.android.synthetic.main.dialog_create_event.view.*
import kotlinx.android.synthetic.main.fragment_diary.*
import kotlinx.android.synthetic.main.item_event.view.*

class DiaryFragment : BaseFragment(R.layout.fragment_diary) {
    private var mAdapter: FirebaseRecyclerAdapter<EventModel, EventsHolder>? = null

    override fun onResume() {
        super.onResume()
        initFields()
        searchEvents()
        Log.d("DATE", diary_date.getDate())
    }

    override fun onPause() {
        super.onPause()
    }

    private fun initFields() {
        diary_date.setOnDateChangedListener { datePicker, i, i2, i3 ->
            diary_events_list.removeAllViewsInLayout()
            searchEvents()
        }

        // Кнопка добавления события (для тестов, далее можете убрать)
        diary_add_events.setOnClickListener {
            diary_events_list.removeAllViewsInLayout()
            createEvent() // Метод добавления события в базу пользователя
        }
    }

    private fun createEvent() {
        val v: View = layoutInflater.inflate(R.layout.dialog_create_event, null)

        MaterialAlertDialogBuilder(APP_MAIN_ACTIVITY)
            .setView(v)
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.save)) { dialog, which ->
                val dateMap = mutableMapOf<String, Any>()
                val eventId =
                    (v.create_event_text.text.toString() + diary_date.getDate()).hashCode()
                        .toString()
                dateMap[EVENT_TEXT] = v.create_event_text.text.toString()
                dateMap[EVENT_DATE] = diary_date.getDate()
                dateMap[EVENT_IS_CHECKED] = "0"
                dateMap[EVENT_ID] = eventId
                REF_DATABASE_ROOT.child(NODE_EVENTS).child(CURRENT_UID)
                    .child(eventId).updateChildren(dateMap).addOnFailureListener {
                        Log.d("Create EVENT ERROR", it.message.toString())
                    }.addOnSuccessListener {
                        searchEvents()
                    }
            }
            .show()
    }

    private fun searchEvents() {
        val query: Query = REF_DATABASE_ROOT.child(NODE_EVENTS).child(CURRENT_UID)
            .orderByChild(EVENT_DATE)
            .equalTo(diary_date.getDate())

        val options = FirebaseRecyclerOptions.Builder<EventModel>()
            .setQuery(query, EventModel::class.java).setLifecycleOwner(this).build()

        mAdapter = object : FirebaseRecyclerAdapter<EventModel, EventsHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_event, parent, false)
                return EventsHolder(view)
            }

            override fun onBindViewHolder(holder: EventsHolder, position: Int, model: EventModel) {
                holder.setValues(model.text, model.date, model.checked)
                Log.d("IsChecked1", model.checked)

                holder.eventIsChecked.setOnCheckedChangeListener { compoundButton, b ->
                    if (b) {
                        REF_DATABASE_ROOT.child(NODE_EVENTS).child(CURRENT_UID).child(model.id)
                            .child(
                                EVENT_IS_CHECKED
                            ).setValue("1").addOnFailureListener {
                                Log.d("State event ERROR", it.message.toString())
                            }.addOnSuccessListener {
                                model.checked = "1"
                            }
                    } else {
                        REF_DATABASE_ROOT.child(NODE_EVENTS).child(CURRENT_UID).child(model.id)
                            .child(
                                EVENT_IS_CHECKED
                            ).setValue("0").addOnFailureListener {
                                Log.d("State event ERROR", it.message.toString())
                            }.addOnSuccessListener {
                                model.checked = "0"
                            }
                    }
                }
            }
        }

        diary_events_list.adapter = mAdapter
        mAdapter?.startListening()
    }

    class EventsHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val eventText: TextView = view.event_text
        private val eventDate: TextView = view.event_date
        val eventIsChecked: CheckBox = view.event_is_checked

        fun setValues(text: String, date: String, isChecked: String) {
            eventText.text = text
            eventDate.text = date

            eventIsChecked.isChecked = isChecked == "1"

            Log.d("IsChecked", isChecked)
        }
    }
}