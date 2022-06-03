package com.galang.smartalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.galang.smartalarm.adapter.AlarmAdapter
import com.galang.smartalarm.data.local.AlarmDB
import com.galang.smartalarm.data.local.AlarmDao
import com.galang.smartalarm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var alarmDao : AlarmDao? = null
    private var alarmAdapter : AlarmAdapter? = null
    private var alarmService : AlarmService? = null

    override fun onResume() {
        super.onResume()
        alarmDao?.getAlarm()?.observe(this) { //data-> //Ganti biar gak pake it
            alarmAdapter?.setData(it)
            Log.i("GetAlarm", "getAlarm : alarm with $it")
        }

//        CoroutineScope(Dispatchers.IO). launch {
//            val alarm = alarmDao?.getAlarm()
//            withContext(Dispatchers.Main){
//                alarm?.let { alarmAdapter?.setData(it) }
//            }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDatabase(applicationContext)
        alarmDao = db.alarmDao()

        alarmAdapter = AlarmAdapter()
        alarmService = AlarmService()

        initView()
        setupRecyclerView()


    }

    private fun setupRecyclerView() {
        binding.rvReminderAlarm.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
            swipeToDelete(this)
        }
    }

    private fun initView() {
        binding.apply {

            cvSetOneTimeAlarm.setOnClickListener {
                startActivity(Intent(applicationContext, OneTimeAlarmActivity::class.java))
            }
            //aplicationContext Sama this@MainActivity itu sama gak ada kekurangan

            cvSetRepeatingAlarm.setOnClickListener {
                startActivity(Intent(this@MainActivity, RepeatingAlarmActivity::class.java))
            }

        }


        //BUAT NAMBAH WAKTU
        //getTimeToday()

    }

//    private fun getTimeToday(){
//        val calendar = Calendar.getInstance() //getInstance() untuk meng inilisasi
//        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault())
//        val time = formattedTime.format(calendar.time)
//
//        binding.tvTimeToday.text = time
//    }



    private fun swipeToDelete(recyclerView: RecyclerView){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            //TODO 3  alarmAdapter?.notifyItemRemoved(viewHolder.adapterPosition )
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedAlarm = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition)
                CoroutineScope(Dispatchers.IO).launch {
                        deletedAlarm?.let { alarmDao?.deleteAlarm(it)}

                    }

                val alarmType = deletedAlarm?.type
                alarmType?.let { alarmService?.cancelAlarm(baseContext, alarmType) }
                alarmAdapter?.notifyItemRemoved(viewHolder.adapterPosition )
            }

        }).attachToRecyclerView(recyclerView)
    }

}