package com.galang.smartalarm.data.localimport androidx.lifecycle.LiveDataimport androidx.room.Daoimport androidx.room.Deleteimport androidx.room.Insertimport androidx.room.Queryimport com.galang.smartalarm.data.Alarm@Daointerface AlarmDao {    @Query("Select * FROM alarm")    fun getAlarm() : LiveData<List<Alarm>>    @Insert    fun addAlarm (alarm: Alarm)    @Delete    fun deleteAlarm(alarm: Alarm)}