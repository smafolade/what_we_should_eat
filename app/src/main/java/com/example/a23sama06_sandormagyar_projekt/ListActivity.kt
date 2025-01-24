package com.example.a23sama06_sandormagyar_projekt

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a23sama06_sandormagyar_projekt.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Customize ActionBar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Visar bakåt-knapp
            setTitle(R.string.list_title) // Sätt titel
        }

        // Få recepten från Intent (det skickas som en String)
        val recipesString = intent.getStringExtra(getString(R.string.recipes)) ?: ""
        val portions = intent.getIntExtra(getString(R.string.portions), 4)

        // Dela upp strängen till en lista
        val recipes = recipesString.split("\n").filter { it.isNotEmpty() }

        // Ställ in RecyclerView med adapter och layout manager via ViewBinding
        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(this)

        // Skapa adapter och sätt den till RecyclerView
        val adapter = RecipeAdapter(this, recipes, R.layout.list_item_recipe, portions) { isLoading ->
            toggleProgressBar(isLoading) // Hantera visning/döljning av ProgressBar
        }
        binding.recipeRecyclerView.adapter = adapter
    }

    // Visa eller dölj ProgressBar
    private fun toggleProgressBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    // Hantera tillbakaknappen för att gå tillbaka till SettingsActivity
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
