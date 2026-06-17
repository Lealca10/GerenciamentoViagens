package com.example.gerenciamentoviagens.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gerenciamentoviagens.viewmodel.FotoViewModel
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(nav: NavHostController, fotoVm: FotoViewModel, viagemId: Int) {
    val context = LocalContext.current
    val fotos by fotoVm.fotos.collectAsState()

    LaunchedEffect(viagemId) {
        fotoVm.carregarFotos(viagemId)
    }

    var tempImageUri by rememberSaveable { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoVm.adicionarFoto(viagemId, it.toString())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null) {
            fotoVm.adicionarFoto(viagemId, tempImageUri!!)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val uri = createTempPictureUri(context)
                tempImageUri = uri.toString()
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                Log.e("PhotosScreen", "Erro ao iniciar câmera", e)
                Toast.makeText(context, "Erro ao iniciar câmera", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fotos da Viagem") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                SmallFloatingActionButton(
                    onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        
                        try {
                            if (hasPermission) {
                                val uri = createTempPictureUri(context)
                                tempImageUri = uri.toString()
                                cameraLauncher.launch(uri)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        } catch (e: Exception) {
                            Log.e("PhotosScreen", "Erro no clique da câmera", e)
                            Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Tirar Foto")
                }
                FloatingActionButton(
                    onClick = { galleryLauncher.launch("image/*") }
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeria")
                }
            }
        }
    ) { padding ->
        if (fotos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Nenhuma foto encontrada.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(fotos) { foto ->
                    AsyncImage(
                        model = foto.uri,
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

fun createTempPictureUri(context: Context): Uri {
    val directory = File(context.cacheDir, "images")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    
    val tempFile = File(directory, "temp_image_${UUID.randomUUID()}.jpg")
    // Algumas câmeras precisam que o arquivo exista
    if (!tempFile.exists()) {
        tempFile.createNewFile()
    }
    
    return FileProvider.getUriForFile(
        context,
        "com.example.gerenciamentoviagens.fileprovider",
        tempFile
    )
}
