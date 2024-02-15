import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class OpenAIAPIService(private val apiKey: String) {
    companion object {
        private const val BASE_URL = "https://api.openai.com/v1/completions"
        private const val DEFAULT_MODEL = "text-davinci-003"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        private const val TEMPERATURE = 0.5
        private const val MAX_TOKENS = 60
        private const val TOP_P = 1.0
        private const val FREQUENCY_PENALTY = 0.0
        private const val PRESENCE_PENALTY = 0.0
    }

    private val client: OkHttpClient = OkHttpClient();

    fun sendRequest(prompt: String, model: String = DEFAULT_MODEL): String? {
        val requestBody = """
            {
                "model": "$model",
                "prompt": "$prompt",
                "temperature": $TEMPERATURE,
                "max_tokens": $MAX_TOKENS,
                "top_p": $TOP_P,
                "frequency_penalty": $FREQUENCY_PENALTY,
                "presence_penalty": $PRESENCE_PENALTY
            }
        """.trimIndent().toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url(BASE_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body?.string()
        }
    }
}
