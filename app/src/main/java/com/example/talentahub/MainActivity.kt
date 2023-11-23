package com.example.talentahub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.talentahub.Repository.RestApi
import com.example.talentahub.models.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), EventAdapter.OnItemClickListener {
    private lateinit var apiEvent: RestApi
    private lateinit var eventList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiEvent = RestApi.create()

        val filterRecyclerView: RecyclerView = findViewById(R.id.filterRecyclerView)
        filterRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val filterList = listOf("Today", "Popular", "Upcoming", "Past","Today", "Popular", "Upcoming", "Past")

        val filterAdapter = FilterAdapter(filterList)
        filterRecyclerView.adapter = filterAdapter

        // Set up RecyclerView with a GridLayoutManager
        val adapter = EventAdapter(emptyList(),this) // Pass an empty list initially
        eventList = findViewById(R.id.recyclerView)
        eventList.adapter = adapter
        eventList.layoutManager = LinearLayoutManager(this)
        fetchEvent()
    }

    override fun onItemClick(event: Event) {
        val intent = Intent(this, DetailsEvent::class.java)
        intent.putExtra("name", event.name)
        intent.putExtra("description", event.description)
        intent.putExtra("location", event.location)
        intent.putExtra("image", event.image)
        startActivity(intent)
    }


    private fun fetchEvent() {
        val call = apiEvent.getAllEvent()
        call.enqueue(object :
            Callback<MutableList<Event>> {

            override fun onResponse(call: Call<MutableList<Event>>, response:
            Response<MutableList<Event>>
            ) {
                if (response.isSuccessful) {
                    val events = response.body()
                    if (events != null) {
                        updateEventList(events)
                    }
                } else {
                    Log.e("hhh", "hhh")
                }
            }

            override fun onFailure(call: Call<MutableList<Event>>, t: Throwable) {
                Log.e("zzz", "zz")
            }
        })
    }
    private fun updateEventList(events: MutableList<Event>) {
        val adapter = eventList.adapter as EventAdapter
        adapter.setData(events)
    }
}