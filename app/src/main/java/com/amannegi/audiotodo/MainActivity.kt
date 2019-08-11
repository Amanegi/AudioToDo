package com.amannegi.audiotodo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.File
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.RECORD_AUDIO
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var recyclerView: RecyclerView

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        setUpDarkMode()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // dark mode
        setUpDarkMode()
        setContentView(R.layout.activity_main)

        if (!mCheckPermissions()) {
            mRequestPermissions()
        }

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            val recordingDialog = RecordingFragment()
            recordingDialog.isCancelable = false
            recordingDialog.show(supportFragmentManager, "RecordingFragment")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val list = getRecordingList()
            recyclerView.adapter = AudioListAdapter(list, getRecyclerViewClickListener(list!!))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_more) {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpDarkMode() {
        // dark mode
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nightMode = sharedPreferences.getBoolean(getString(R.string.keyDarkMode), false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun getRecordingList(): Array<out File>? {
        val directory = File(RecordingFragment.FOLDER_PATH)
        return directory.listFiles()
    }

    private fun getRecyclerViewClickListener(list: Array<out File>): RecyclerViewClickListener {
        return object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                val audioPlayDialog = AudioPlayFragment()
                audioPlayDialog.isCancelable = false
                val bundle = Bundle()
                bundle.putString(KEY_NAME, list[position].name)
                bundle.putString(KEY_PATH, list[position].absolutePath)
                audioPlayDialog.arguments = bundle
                audioPlayDialog.show(supportFragmentManager, "AudioPlayFragment")
            }

        }
    }

    private fun mCheckPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun mRequestPermissions() {
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE), 0)
    }

    companion object {
        const val KEY_NAME = "name"
        const val KEY_PATH = "path"
    }

}
