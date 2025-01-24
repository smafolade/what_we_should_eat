package com.example.a23sama06_sandormagyar_projekt

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.a23sama06_sandormagyar_projekt.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedLanguage = ""
    private var selectedPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Spinner
        val languages = listOf(
            getString(R.string.lang_eng),
            getString(R.string.lang_swe),
            getString(R.string.lang_de),
            getString(R.string.lang_es),
            getString(R.string.lang_hu),
            getString(R.string.lang_sr)
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.languageDropdown.adapter = adapter

        selectedLanguage = getString(R.string.langcode_en)
        // Spinner interaction listener
        binding.languageDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val languageCode = when (position) {
                    0 -> getString(R.string.langcode_en)
                    1 -> getString(R.string.langcode_sv)
                    2 -> getString(R.string.langcode_de)
                    3 -> getString(R.string.langcode_es)
                    4 -> getString(R.string.langcode_hu)
                    5 -> getString(R.string.langcode_sr)
                    else -> getString(R.string.langcode_en)
                }
                selectedLanguage = languageCode
                selectedPos = position
                changeLanguage(languageCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Set up button click listener
        binding.nextButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    // Change app language
    private fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        updateUI()
        selectedLanguage = languageCode
    }

    private fun updateUI() {
        binding.nextButton.text = getString(R.string.next_button)
        binding.languageDropdown.setSelection(selectedPos)
        title = getString(R.string.app_name)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.close_app -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
