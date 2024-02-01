package fr.unilim.saes5.persistence.lang

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class JsonLangDao : LangDAO {
    private val messages: Map<String, String>

    init {
        val inputStream = javaClass.classLoader.getResourceAsStream("messages.json")
            ?: throw IllegalArgumentException("messages.json not found")

        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<Map<String, String>>() {}.type
        messages = Gson().fromJson(reader, type)
        reader.close()
    }

    override fun getMessage(key: String): String {
        return messages[key] ?: key
    }
}
