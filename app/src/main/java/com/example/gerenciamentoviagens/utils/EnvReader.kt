package com.example.gerenciamentoviagens.utils

import android.content.Context
import java.util.Properties

object EnvReader {
    fun getApiKey(context: Context): String {
        return try {
            val properties = Properties()
            // Procura no assets pelo arquivo key.env
            context.assets.open("key.env").use { properties.load(it) }
            properties.getProperty("GEMINI_API_KEY") ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
