package fr.unilim.codelinguo.common.service

import com.cjcrafter.openai.chat.ChatMessage.Companion.toSystemMessage
import com.cjcrafter.openai.chat.ChatMessage.Companion.toUserMessage
import com.cjcrafter.openai.chat.chatRequest
import com.cjcrafter.openai.openAI
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class OpenAIAPIService {
    companion object {
        private const val DEFAULT_MODEL = "gpt-3.5-turbo"
    }


    private val customHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(3, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .build()

    fun sendRequest(prompt: String, data: String, apiKey: String): String? {
        try {
            val openai = openAI {
                apiKey(apiKey)
                client(customHttpClient)
            }

            val request = chatRequest {
                model(DEFAULT_MODEL)
                addMessage(prompt.toSystemMessage())
                addMessage(data.toUserMessage())
            }

            return openai.createChatCompletion(request)[0].message.content
        } catch (e: Exception) {
            return null
        }
    }
}