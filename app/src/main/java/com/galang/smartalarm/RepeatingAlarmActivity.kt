package com.galang.smartalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.galang.smartalarm.data.Alarm
import com.galang.smartalarm.data.local.AlarmDB
import com.galang.smartalarm.data.local.AlarmDao
import com.galang.smartalarm.databinding.ActivityRepeatingAlarmBinding
import com.galang.smartalarm.fragment.TimePickerFragment
import com.galang.smartalarm.helper.TAG_TIME_PICKER
import com.galang.smartalarm.helper.timeFormatter
import kotlinx.android.synthetic.main.activity_one_time_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity(), TimePickerFragment.TimeDialogListener {

    private var _binding : ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding

    private var alarmDao : AlarmDao? = null

    private var _alarmService : AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_repeating_alarm)

        val db = AlarmDB.getDatabase(this)
        alarmDao = db.alarmDao()

        _alarmService = AlarmService()

        initView()
    }

    private fun initView() {
        binding.apply {
            
            btnSetTimeRepeating.setOnClickListener {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, TAG_TIME_PICKER)
            }

            btnAddSetRepeatingAlarm.setOnClickListener {
                val time = tv_once_time.text.toString()
                val message = etNoteRepeating.text.toString()

                if (time != "Time"){
                    alarmService.setRepeatingAlarm(
                        applicationContext,
                        AlarmService.TYPE_REPEATING,
                        time,
                        message
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao?.addAlarm(Alarm(
                            0,
                            "Repeating Alarm",
                            time,
                            message,
                            AlarmService.TYPE_REPEATING
                        ))
                            finish()
                    }
                }else{
                    Toast.makeText(this@RepeatingAlarmActivity,
                    "Please set time of alarm.",
                    Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnCancelSetRepeatingAlarm.setOnClickListener {
                finish()
            }
        }
    }

    override fun onDialogTimeSet(tag: String?, hoursOfDay: Int, minute: Int) {
        binding.tvRepeatingTime.text = timeFormatter(hoursOfDay, minute)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}