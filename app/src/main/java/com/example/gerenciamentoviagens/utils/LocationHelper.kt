package com.example.gerenciamentoviagens.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.Locale

class LocationHelper(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLocalizacaoAtualFlow(): Flow<android.location.Location?> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                trySend(location)
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                trySend(location)
            }
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    @SuppressLint("MissingPermission")
    fun getCidadeAtualFlow(): Flow<String?> = callbackFlow {
        val geocoder = Geocoder(context, Locale("pt", "BR"))

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return

                launch(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val address = addresses?.firstOrNull()

                        val cidade = address?.locality
                            ?: address?.subAdminArea
                            ?: address?.subLocality
                            ?: address?.adminArea

                        if (cidade != null) {
                            Log.d("LocationHelper", "Cidade atualizada: $cidade")
                            trySend(cidade.normalizar())
                        }
                    } catch (e: Exception) {
                        Log.e("LocationHelper", "Erro no Geocoder", e)
                    }
                }
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(500)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                launch(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val cidade = addresses?.firstOrNull()?.locality
                        if (cidade != null) trySend(cidade.normalizar())
                    } catch (e: Exception) {
                        Log.e("LocationHelper", "Erro no lastLocation Geocoder", e)
                    }
                }
            }
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

private fun String.normalizar(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .lowercase()
        .trim()