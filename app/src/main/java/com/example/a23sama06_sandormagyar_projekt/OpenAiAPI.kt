package com.example.a23sama06_sandormagyar_projekt

import android.content.Context
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType

class OpenAiApi(private val context: Context) {

    private val client = OkHttpClient()
    private val gson = Gson()

    private val apiKey = context.getString(R.string.apikey)


    fun sendQuery(query: String, callback: (String?, Exception?) -> Unit) {
        val json = JsonObject().apply {
            addProperty(context.getString(R.string.model), context.getString(R.string.gpt_3_5_turbo))
            add(
                context.getString(R.string.messages), gson.toJsonTree(listOf(
                    mapOf(context.getString(R.string.role) to context.getString(R.string.system),
                        context.getString(
                            R.string.content
                        ) to context.getString(R.string.you_are_a_helpful_assistant)),
                    mapOf(context.getString(R.string.role) to context.getString(R.string.user), context.getString(
                        R.string.content
                    ) to query)
                )))
        }

        val body = RequestBody.create(context.getString(R.string.application_json).toMediaType(), json.toString())
        val request = Request.Builder()
            .url(context.getString(R.string.https_api_openai_com_v1_chat_completions))
            .addHeader(context.getString(R.string.authorization),
                context.getString(R.string.bearer, apiKey))
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        if (it.code == 429) {
                            // Kontrollera om Retry-After header finns
                            val retryAfter = it.header(context.getString(R.string.retry_after))?.toLongOrNull() ?: 5 // Standardväntetid om ingen header finns

                            // Vänta i det angivna antalet sekunder innan nästa försök
                            Thread.sleep(retryAfter * 1000)

                            // Försök skicka förfrågan igen efter väntan
                            sendQuery(query, callback)
                        } else {
                            callback(null, IOException(
                                context.getString(
                                    R.string.unexpected_response_code,
                                    response
                                )))
                        }
                    } else {
                        // Hantera lyckad förfrågan
                        val responseBody = it.body?.string()
                        val result = gson.fromJson(responseBody, JsonObject::class.java)
                        val reply = result[context.getString(R.string.choices)]?.asJsonArray?.get(0)?.asJsonObject
                            ?.get(context.getString(R.string.message))?.asJsonObject
                            ?.get(context.getString(R.string.content))?.asString

                        val recipes = parseResponseToList(reply)

                        val recipesString = recipes.joinToString("\n")

                        callback(recipesString, null) // Skicka tillbaka som sträng
                    }
                }
            }
        })
    }

    // Funktion för att dela upp svaret i en lista
    private fun parseResponseToList(response: String?): List<String> {
        return if (!response.isNullOrEmpty()) {
            // Dela upp svaret till en lista där varje recept är ett element
            response.split("\n") // Dela upp efter radbrytningar
                .map { it.trim() } // Ta bort eventuella extra mellanslag
                .filter { it.isNotEmpty() } // Ta bort tomma rader
        } else {
            emptyList()
        }
    }
}
