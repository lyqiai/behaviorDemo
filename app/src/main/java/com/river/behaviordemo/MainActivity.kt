package com.river.behaviordemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.river.behaviordemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBtn.setOnClickListener {
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SecActivity::class.java))
        }

        binding.actionSw.setOnCheckedChangeListener { buttonView, isChecked ->  }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            Toast.makeText(this, "$checkedId", Toast.LENGTH_SHORT).show()
            AlertDialog.Builder(this)
                    .setTitle("title")
                    .setPositiveButton("ok") {dialog, id -> }
                    .setNegativeButton("ok") {dialog, id -> }
                    .show()
        }

        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(this, "$isChecked", Toast.LENGTH_SHORT).show()
            DemoDialog().show(supportFragmentManager, "DIALOG_FRAGMENT_DEMO")
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            Toast.makeText(this, "$checkedId", Toast.LENGTH_SHORT).show()
        }

        binding.toggleButton.setOnClickListener {
            Toast.makeText(this, "toggle", Toast.LENGTH_SHORT).show()
        }

        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->

        }

        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }
}