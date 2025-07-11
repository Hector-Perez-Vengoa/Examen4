package com.perez.hector.lab13_perez

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.perez.hector.lab13_perez.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    // Se declara imgCaptureExecutor para ejecutar tareas relacionadas con la captura de imágenes en un hilo separado.
    // Observación: Usar ExecutorService mejora el rendimiento y evita bloquear el hilo principal.
    // Conclusión: Esta implementación es adecuada para operaciones de cámara que requieren procesamiento en segundo plano.
    private lateinit var imgCaptureExecutor: ExecutorService
    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                startCamera()
            } else {
                Snackbar.make(
                    binding.root,
                    "The camera permission is necessary",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se inicializa el binding y se establece el layout principal.
        // Se obtiene el proveedor de la cámara y se selecciona la cámara trasera.
        // Se inicializa imgCaptureExecutor para tareas de captura de imágenes.
        // Se solicita el permiso de cámara al usuario.
        // Observación: El flujo garantiza que la cámara solo se inicia si el usuario concede el permiso.
        // Conclusión: El manejo de permisos y la inicialización de recursos están correctamente estructurados para una app de cámara.
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        cameraPermissionResult.launch(Manifest.permission.CAMERA)


        binding.imgCaptureBtn.setOnClickListener {
            takePhoto()
        }

        binding.switchBtn.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }

        binding.galleryBtn.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    // Función startCamera: Inicializa y configura la cámara, el preview y la captura de imágenes.
    // Observación: Se utiliza CameraX para gestionar el ciclo de vida de la cámara y vincular los casos de uso.
    // Conclusión: Permite mostrar la vista previa y preparar la captura de fotos de manera eficiente.
    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.preview.surfaceProvider)
        }
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // Función takePhoto: Realiza la captura de una foto y la guarda en el almacenamiento externo.
    // Observación: Utiliza ImageCapture de CameraX y ejecuta la tarea en un hilo separado.
    // Conclusión: Permite tomar fotos sin bloquear el hilo principal y maneja errores de captura.
    private fun takePhoto() {
        imageCapture?.let {
            val fileName = "JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i(TAG, "The image has been saved in "+file.toURI())
                    }
                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(binding.root.context, "Error taking photo", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Error taking photo: $exception")
                    }
                }
            )
        }
    }

    companion object {
        val TAG = "MainActivity"
    }
}