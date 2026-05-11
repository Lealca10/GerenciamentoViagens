package com.example.gerenciamentoviagens.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LocationHelper(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCidadeAtual(): String? {
        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()

            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                // Geocoder.getFromLocation is deprecated in API 33+, but for simplicity in this exercise 
                // we use the synchronous version. In a real app, we'd use the callback version.
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                addresses?.firstOrNull()?.locality
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
