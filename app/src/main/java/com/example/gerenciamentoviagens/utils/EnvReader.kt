package com.example.gerenciamentoviagens.utils

import android.content.Context
import android.util.Log

object EnvReader {
    fun getApiKey(context: Context): String {
        return try {
            val content = context.assets.open("key.env").bufferedReader().use { it.readText() }
            val keyLine = content.lines().find { it.contains("GEMINI_API_KEY=") }
            val key = keyLine?.substringAfter("GEMINI_API_KEY=")
                ?.trim()
                ?.removeSurrounding("\"")
                ?.removeSurrounding("'") ?: ""
            
            if (key.isEmpty()) {
                Log.e("EnvReader", "ERRO: GEMINI_API_KEY não encontrada no key.env")
            }
            key
        } catch (e: Exception) {
            Log.e("EnvReader", "Erro ao acessar assets/key.env", e)
            ""
        }
    }
}
