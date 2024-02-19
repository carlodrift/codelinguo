import fr.unilim.codelinguo.model.Word
import com.cjcrafter.openai.chat.*
import com.cjcrafter.openai.chat.ChatMessage.Companion.toSystemMessage
import com.cjcrafter.openai.chat.ChatMessage.Companion.toUserMessage
import com.cjcrafter.openai.openAI
import io.github.cdimascio.dotenv.dotenv
import okhttp3.OkHttpClient
import java.net.http.HttpClient
import java.util.concurrent.TimeUnit

class OpenAIAPIService() {
    companion object {
        private const val DEFAULT_MODEL = "gpt-3.5-turbo" // gpt-4-0125-preview
    }

    private fun getApiKey(): String {
        val dotenv = dotenv()
        return dotenv["OPEN_AI_API_KEY"]
    }


    private val customHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)  // Customize as needed
        .readTimeout(30, TimeUnit.SECONDS)     // Customize as needed
        .writeTimeout(15, TimeUnit.SECONDS)    // Customize as needed
        .build()

    fun sendRequest(prompt: String, data : String): String? {
        val openai = openAI {
            apiKey(getApiKey())
            client(customHttpClient)
        }

        val request = chatRequest {
            model(DEFAULT_MODEL)
            addMessage(prompt.toSystemMessage())
            addMessage(data.toUserMessage())
        }

        return openai.createChatCompletion(request)[0].message.content
    }


}
