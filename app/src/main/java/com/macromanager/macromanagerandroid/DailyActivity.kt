package com.macromanager.macromanagerandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem


class DailyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {

            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)

                startActivity(settingsIntent)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
