package com.example.a23sama06_sandormagyar_projekt

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.a23sama06_sandormagyar_projekt.databinding.ListItemRecipeBinding

class RecipeAdapter(
    private val context: Context,
    private val recipes: List<String>,
    private val itemLayout: Int,
    private val portions: Int,
    private val onLoadingStateChange: (Boolean) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val openAiApi = OpenAiApi(context)

    // ViewHolder för att hålla referenser till vyn
    inner class RecipeViewHolder(private val binding: ListItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: String) {
            binding.recipeTextView.text = recipe

            // Hantera klick för varje item
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    fetchRecipeDetails(recipe)
                }
            }
        }
    }

    // Returnera en instans av RecipeViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ListItemRecipeBinding.inflate(LayoutInflater.from(context), parent, false)
        return RecipeViewHolder(binding)
    }

    // Bind receptdata till vyerna
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    // Funktion som anropas när ett recept är klickat
    private fun fetchRecipeDetails(recipe: String) {
        // Visa ProgressBar (på huvudtråden)
        (context as? ListActivity)?.runOnUiThread {
            onLoadingStateChange(true)
        }

        val query = context.getString(R.string.recipe_detail_query, recipe, portions)

        // Skicka fråga till OpenAiApi
        openAiApi.sendQuery(query) { response, exception ->
            // Dölj ProgressBar (på huvudtråden)
            (context as? ListActivity)?.runOnUiThread {
                onLoadingStateChange(false)
            }

            if (exception != null) {
                (context as? ListActivity)?.runOnUiThread {
                    Toast.makeText(context,
                        context.getString(R.string.error, exception.message), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Öppna RecipeDetailActivity med svaret (på huvudtråden)
                (context as? ListActivity)?.runOnUiThread {
                    val intent = Intent(context, RecipeDetailActivity::class.java)
                    intent.putExtra(context.getString(R.string.recipedetails), response)
                    context.startActivity(intent)
                }
            }
        }
    }
}
