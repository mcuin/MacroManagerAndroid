package com.cuinsolutions.macrosmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar
import java.util.Locale

/**
 * Created by mykalcuin on 9/21/17.
 */

class DeviceRebootDailyResetAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        if (p1!!.action == ("android.intent.action.BOOT_COMPLETED")) {

            val resetTime = Calendar.getInstance(Locale.getDefault())
            resetTime.timeInMillis = System.currentTimeMillis()
            resetTime.set(Calendar.HOUR_OF_DAY, 2)

            val resetIntent = Intent(p0, DailyResetAlarmReciever::class.java)
            val resetPendingIntent = PendingIntent.getBroadcast(p0, 0, resetIntent, 0)
            val resetAlarm = p0!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            resetAlarm.setInexactRepeating(AlarmManager.RTC, resetTime.timeInMillis, AlarmManager.INTERVAL_DAY, resetPendingIntent)
        }
    }


}