package com.example.gerenciamentoviagens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "fotos",
    foreignKeys = [
        ForeignKey(
            entity = Viagem::class,
            parentColumns = ["id"],
            childColumns = ["viagemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Foto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val viagemId: Int,
    val uri: String // URI of the photo in the device
)
