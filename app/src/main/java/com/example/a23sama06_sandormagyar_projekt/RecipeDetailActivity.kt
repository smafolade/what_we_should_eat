package com.example.a23sama06_sandormagyar_projekt

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.a23sama06_sandormagyar_projekt.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Customize ActionBar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Visar bakåt-knapp
            setTitle(R.string.recipe_details) // Sätt titel
        }

        // Hämta receptdetaljer från Intent
        val recipeDetails = intent.getStringExtra(getString(R.string.recipedetails)) ?: getString(R.string.no_details_available)

        // Visa detaljerna i TextView
        binding.recipeTextView.text = recipeDetails

        // Sätt klicklistener för knappen
        binding.backToSettingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Avsluta RecipeDetailActivity
        }
    }

    // Hantera tillbakaknappen för att gå tillbaka till Receptlistan
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Ladda menyn från menu_main.xml
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle the menu actions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.close_app -> {
                finishAffinity() // Stänger appen
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
