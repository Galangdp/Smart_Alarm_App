package com.galang.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.galang.smartalarm.data.Alarm
import com.galang.smartalarm.data.local.AlarmDB
import com.galang.smartalarm.data.local.AlarmDao
import com.galang.smartalarm.databinding.ActivityOneTimeAlarmBinding
import com.galang.smartalarm.fragment.DatePickerFragment
import com.galang.smartalarm.fragment.TimePickerFragment
import com.galang.smartalarm.helper.TAG_TIME_PICKER
import com.galang.smartalarm.helper.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(), DatePickerFragment.DateDialogListener,
    TimePickerFragment.TimeDialogListener {

    private var _binding : ActivityOneTimeAlarmBinding? = null
    private val binding get() = _binding as ActivityOneTimeAlarmBinding

    private var alarmDao : AlarmDao? = null

    private var _alarmService : AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_time_alarm)

        _binding = ActivityOneTimeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDatabase(this)
        alarmDao = db.alarmDao()

        _alarmService = AlarmService()

        initView()

    }

    private fun initView() {
        binding.apply {

            btnSetDateOneTime.setOnClickListener {
                val datePickerFragment = DatePickerFragment()
                //Show buat nampilinnya
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }

            btnSetTimeOneTime.setOnClickListener {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, TAG_TIME_PICKER)
            }

            btnAddSetOneTimeAlarm.setOnClickListener{
                val date = tvOnceDate.text.toString()
                val time = tvOnceTime.text.toString()
                val message = setNoteOneTime.text.toString()

                if(date != "Date" && time != "Time"){

                    alarmService.setOneTimeAlarm(applicationContext,1, date, time, message)

                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao?.addAlarm(Alarm(0, date, time, message,AlarmService.TYPE_ONE_TIME))

                        Log.i("AddAlarm", "Succes set alarm on $date $time with message $message")
                        finish()
                        }
                    }else{
                    Toast.makeText(applicationContext, "Set your Date & Time.", Toast.LENGTH_SHORT).show()
                }


            }

        }
    }


    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMounth: Int) {
        val calendar = Calendar.getInstance()
            // Mengatur tanggal supaya sama dengan yang sudah di pilih di DatePickerDialog
        calendar.set(year, month, dayOfMounth)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.tvOnceDate.text = dateFormat.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hoursOfDay: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hoursOfDay, minute)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}