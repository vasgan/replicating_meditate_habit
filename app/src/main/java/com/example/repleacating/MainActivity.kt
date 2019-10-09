package com.example.repleacating

import android.animation.Animator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity  : AppCompatActivity() {
    private val name = arrayOf("Sleep", "Morning", "Commute", "Walking", "At work", "Big Event","SOS","Tough Day","After work","Talking a Break")
    private val resource = arrayOf(R.drawable.baseline_hotel_white_18dp, R.drawable.baseline_local_florist_white_18dp, R.drawable.baseline_directions_bus_white_18dp,
            R.drawable.baseline_directions_walk_white_18dp, R.drawable.baseline_work_white_18dp,R.drawable.baseline_calendar_today_white_18dp,
            R.drawable.baseline_error_outline_white_18dp, R.drawable.baseline_sentiment_dissatisfied_white_18dp, R.drawable.baseline_home_white_18dp, R.drawable.baseline_local_cafe_white_18dp)
    private val color = intArrayOf(R.color.first_color, R.color.second_color, R.color.third_color, R.color.first_color,R.color.second_color, R.color.third_color,R.color.first_color, R.color.second_color,R.color.third_color, R.color.second_color)
    private var baseDates: ArrayList<PieBeat>? = null
    private var colors: ArrayList<Int>? = null
    private val nameSleep = arrayOf("More...", "Going to Sleep", "Restless", "Can't Sleep")
    private val nameMorning = arrayOf("Morning meditation", "Good morning", "commuting", "Improve Mood", "More...")
    private val takingBreak = arrayOf("Work break", "Talking a Bath", "Stressed", "Improve Focus", "Eating", "More...")
    private val commute = arrayOf("Bus or Train", "Car", "Walking", "Waiting", "More...")
    private val walking = arrayOf("In the City", "Parks & Nature", "Commuting", "More...")
    private val atWork = arrayOf("Stressed", "Frustrated", "Improve Focus", "Work Break", "Lunch Break", "More...")
    private val bigEvent = arrayOf("Feeling Nervous", "Public Speaking", "Test or Interview", "Before A Difficult Conversation", "More...")
    private val sos = arrayOf("Depressed", "Nervous", "Frustrated", "Pain", "Panic Attack", "More...")
    private val toughDay = arrayOf("Stressed", "Depressed", "Feeling Alone", "Need a Purpose", "Feeling Under the Weather", "More...")
    private val afterWork = arrayOf("Just Got Home", "Deep Relaxation", "Peaceful Evening", "More...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        baseDates = ArrayList<PieBeat>()
        colors = ArrayList<Int>()
        setView()
        backgroundView.setOnClickListener {
            backgroundView
                    .animate()
                    .alpha(0f)
                    .duration = 250
            secondPieView.animate()
                    .scaleX(0.1F)
                    .scaleY(0.1F)
                    .setDuration(250)
                    .setListener(object : Animator.AnimatorListener{
                        override fun onAnimationRepeat(p0: Animator?) {
                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            secondPieView.visibility = View.GONE
                            backgroundView.visibility = View.GONE
                        }

                        override fun onAnimationCancel(p0: Animator?) {
                        }

                        override fun onAnimationStart(p0: Animator?) {
                        }
                    })
        }
    }
    private fun setView(){
        pieView?.setTextColor(Color.WHITE)
        pieView?.setCenterInnerCir(2)
        pieView?.setCenterText("What are you","doing?")
        pieView?.setCenterTextColor(ContextCompat.getColor(this, R.color.bg_white))
        pieView?.setShowImage(true)
        pieView?.setClickListener(object : OnPieClick{
            override fun onClick(position: Int) {
                showSeccondPieView(position)
            }
        })

        for (i in color.indices) {
            colors?.add(color[i])
            val pieBean = PieBeat()
            pieBean.name = name[i]
            pieBean.drawable = resource[i]
            baseDates?.add(pieBean)
        }
        colors?.let { pieView?.setmColors(it) }
        baseDates?.let { pieView?.setData(it) }
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    fun showSeccondPieView(position: Int){
        secondPieView.alpha = 0.5f
        secondPieView.scaleX = 0.0f
        secondPieView.scaleY = 0.0f
        secondPieView.visibility = View.VISIBLE
        secondPieView.animate()
                .scaleX(1F)
                .scaleY(1F)
                .alpha(1f)
                .setDuration(250)
                .setListener(null)
        backgroundView.visibility = View.VISIBLE
        backgroundView.alpha = 0.0f
        backgroundView.animate()
                .alpha(1f)
                .setDuration(250)
                .setListener(null)
        secondPieView?.setTextColor(Color.WHITE)
        colors?.let { secondPieView?.setmColors(it) }
        val baseDates = ArrayList<PieBeat>()
        var strings = when (position) {
            0 ->  nameSleep
            1 -> nameMorning
            2 -> takingBreak
            3 -> commute
            4 -> walking
            5 -> atWork
            6 -> bigEvent
            7 -> sos
            8 -> toughDay
            9 -> afterWork
            else -> arrayOf("null")
        }
        for (i in strings.indices) {
            val pieBean = PieBeat()
            pieBean.name = strings[i]
            baseDates.add(pieBean)
        }
        secondPieView?.setData(baseDates)
    }
}
