package com.example.a23sama06_sandormagyar_projekt

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a23sama06_sandormagyar_projekt.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var openAiApi: OpenAiApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openAiApi = OpenAiApi(this)

        // Set the title of the activity
        title = getString(R.string.settings_title)

        // Customize ActionBar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Show back button
        }

        // Populate spinners with arrays from resources
        setupSpinners()

        // Handle button click for search action
        binding.searchButton.setOnClickListener {
            val query = buildQueryFromSettings()
            val portions = binding.portionsSpinner.selectedItem.toString().toInt()

            binding.progressBar.visibility = View.VISIBLE

            // Skicka frågan till ChatGPT
            openAiApi.sendQuery(query) { response, exception ->
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE

                    if (exception != null) {
                        Toast.makeText(this,
                            getString(R.string.error, exception.message), Toast.LENGTH_SHORT).show()
                    } else {
                       // Omvandla strängen till en lista (om det är en kommaseparerad sträng)
                        val recipesList = response?.split(",")?.map { it.trim() } ?: emptyList()

                        // Skicka recepten vidare till ListActivity
                        val intent = Intent(this, ListActivity::class.java)
                        intent.putExtra(getString(R.string.recipes), response)
                        intent.putExtra(getString(R.string.portions), portions)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun setupSpinners() {
        // Holiday Spinner
        val holidaysAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.holidays_array,
            android.R.layout.simple_spinner_item
        )
        holidaysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.holidaySpinner.adapter = holidaysAdapter

        // Portions Spinner
        val portionsAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.portions_array,
            android.R.layout.simple_spinner_item
        )
        portionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.portionsSpinner.adapter = portionsAdapter

        // Recipe Count Spinner
        val recipeCountAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.recipe_count_array,
            android.R.layout.simple_spinner_item
        )
        recipeCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.recipeCountSpinner.adapter = recipeCountAdapter
    }

    private fun buildQueryFromSettings(): String {
        val holiday = binding.holidaySpinner.selectedItem.toString()
        val portions = binding.portionsSpinner.selectedItem.toString()
        val recipeCount = binding.recipeCountSpinner.selectedItem.toString()

        val dietTypes = mutableListOf<String>()
        if (binding.lactoseFreeCheckbox.isChecked) dietTypes.add(getString(R.string.lactose_free))
        if (binding.glutenFreeCheckbox.isChecked) dietTypes.add(getString(R.string.gluten_free))
        if (binding.vegetarianCheckbox.isChecked) dietTypes.add(getString(R.string.vegetarian))
        if (binding.diabetesCheckbox.isChecked) dietTypes.add(getString(R.string.diabetes))

        val dietString = if (dietTypes.isNotEmpty()) dietTypes.joinToString(", ") else "None"

        return getString(R.string.query_template, recipeCount, holiday, portions, dietString)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Close activity
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.close_app -> {
                finishAffinity() // Close the app
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
