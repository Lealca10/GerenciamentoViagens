package com.example.gerenciamentoviagens.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gerenciamentoviagens.data.local.dao.FotoDao
import com.example.gerenciamentoviagens.data.local.dao.UsuarioDao
import com.example.gerenciamentoviagens.data.local.dao.ViagemDao
import com.example.gerenciamentoviagens.data.local.entity.Foto
import com.example.gerenciamentoviagens.data.local.entity.Usuario
import com.example.gerenciamentoviagens.data.local.entity.Viagem

@Database(entities = [Usuario::class, Viagem::class, Foto::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun viagemDao(): ViagemDao
    abstract fun fotoDao(): FotoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "db_viagens_v4" // Novo nome para garantir reset total
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
