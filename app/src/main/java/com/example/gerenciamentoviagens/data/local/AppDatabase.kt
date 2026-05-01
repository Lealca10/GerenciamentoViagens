package com.example.gerenciamentoviagens.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gerenciamentoviagens.data.local.dao.UsuarioDao
import com.example.gerenciamentoviagens.data.local.dao.ViagemDao
import com.example.gerenciamentoviagens.data.local.entity.Usuario
import com.example.gerenciamentoviagens.data.local.entity.Viagem

@Database(entities = [Usuario::class, Viagem::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun viagemDao(): ViagemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration() // Facilitando para fins de desenvolvimento inicial
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
