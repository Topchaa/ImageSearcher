package com.example.imagesearcher


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.bumptech.glide.Glide
import com.example.imagesearcher.model.Hit
import com.example.imagesearcher.model.pixabay
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    private val datalist : MutableList<Hit> = mutableListOf()
    private lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
            IntentFilter("custom-message")
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "notify"
            val descriptionText = "pics"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        supportActionBar!!.hide()
        customAdapter = CustomAdapter(datalist)
        CustomList.adapter = customAdapter
        CustomList.layoutManager = LinearLayoutManager(this)
        CustomList.addItemDecoration(DividerItemDecoration(this, OrientationHelper.VERTICAL))

        AndroidNetworking.initialize(this)
        var searchFor = "coding"

        search_button.setOnClickListener(){

            datalist.clear()
            searchFor = search_bar.text.toString()



            AndroidNetworking.get("https://pixabay.com/api/?key=19883330-5da8bfed475cbd6daa6aa02ca&q="+searchFor+"&image_type=photo&pretty=true")
                .build()
                .getAsObject(pixabay::class.java , object : ParsedRequestListener<pixabay> {
                    override fun onResponse(response: pixabay?) {
                        datalist.addAll(response!!.hits)
                        customAdapter.notifyDataSetChanged()
                    }

                    override fun onError(anError: ANError?) {
                        Toast.makeText(this@MainActivity,"No photos found", Toast.LENGTH_SHORT).show()
                    }
                })
            search_bar.text.clear()


        }

        AndroidNetworking.get("https://pixabay.com/api/?key=19883330-5da8bfed475cbd6daa6aa02ca&q="+searchFor+"&image_type=photo&pretty=true")
            .build()
            .getAsObject(pixabay::class.java , object : ParsedRequestListener<pixabay> {
                override fun onResponse(response: pixabay?) {
                    datalist.addAll(response!!.hits)
                    customAdapter.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(this@MainActivity,"No photos found", Toast.LENGTH_SHORT).show()
                }
            })

        fullscreen.setOnClickListener(){

            fullscreen.visibility = View.GONE

        }

    }


    var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {


            val imageLink = intent.getStringExtra("links")

            fullscreen.visibility = View.VISIBLE

            Glide.with(this@MainActivity)
                .load(imageLink)
                .into(fullScreenImage)



        }


    }




}