package com.perez.hector.lab13_perez

import GalleryAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.perez.hector.lab13_perez.databinding.ActivityGalleryBinding
import java.io.File

// Clase GalleryActivity: Muestra una galería de imágenes almacenadas localmente.
// Observación: Se utiliza ViewBinding para acceder de forma segura a las vistas del layout.
// Se emplea un adaptador personalizado (GalleryAdapter) para mostrar las imágenes en un ViewPager.
// Conclusión: La implementación facilita la visualización de imágenes y mejora la experiencia de usuario mediante navegación deslizante.
// Además, el uso de reversedArray() permite mostrar las imágenes más recientes primero.

class GalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val directory = File(externalMediaDirs[0].absolutePath)
        val files = directory.listFiles() as Array<File>

        val adapter = GalleryAdapter(files.reversedArray())
        binding.viewPager.adapter = adapter
    }
}
