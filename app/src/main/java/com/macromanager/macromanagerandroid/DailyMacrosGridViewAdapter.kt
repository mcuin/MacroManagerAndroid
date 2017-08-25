package com.macromanager.macromanagerandroid

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by mykalcuin on 8/23/17.
 */


class DailyMacrosGridViewAdapter(context: Context, macrosArray: ArrayList<JSONObject>) : BaseAdapter() {

    /*val context = context*/
    val macrosArray = macrosArray
    val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItem(p0: Int): Any? {
        return macrosArray[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return macrosArray.size
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        var macroGridCell = p1
        if (p1 == null) {

            macroGridCell = inflate.inflate(R.layout.intake_grid_cell, p2, false)
        }

        val macroTitleTextView = macroGridCell!!.findViewById<TextView>(R.id.macroTitleTextView)
        val macroCurrentTextView = macroGridCell!!.findViewById<TextView>(R.id.macroCurretTextView)

        macroTitleTextView.text = (getItem(p0) as JSONObject).getString("title").toString()
        macroCurrentTextView.text = (macrosArray[p0] as JSONObject).getString("currentTotal").toString() + " / " +
                (macrosArray[p0] as JSONObject).getString("dailyTotal").toString()

        Log.d("Title", macrosArray[p0].getString("title"))
        Log.d("Current", macrosArray[p0].getString("currentTotal"))
        Log.d("Daily", macrosArray[p0].getString("dailyTotal"))

        return  macroGridCell
    }


}