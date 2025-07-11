// Adaptador para mostrar imágenes en un RecyclerView usando archivos locales.
// Se utiliza ViewBinding para acceder a las vistas de cada ítem.
// Glide se emplea para cargar imágenes de manera eficiente.
// Observación: El adaptador recibe un array de archivos y los muestra en la lista.
// Conclusión: Este enfoque facilita la visualización de imágenes locales y el uso de ViewBinding mejora la seguridad y legibilidad del código.
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.perez.hector.lab13_perez.databinding.ListItemImgBinding
import java.io.File

class GalleryAdapter(private val fileArray: Array<File>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ListItemImgBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            Glide.with(binding.root).load(file).into(binding.localImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ListItemImgBinding.inflate(layoutInflater, parent, false))
    }

    // Método onBindViewHolder: Asocia cada archivo de imagen con el ViewHolder correspondiente.
    // Observación: Se utiliza el método bind para cargar la imagen en la vista.
    // Conclusión: El proceso de vinculación es eficiente y aprovecha Glide para la carga de imágenes.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fileArray[position])
    }

    // Método getItemCount: Devuelve la cantidad de archivos a mostrar.
    // Observación: Permite al RecyclerView saber cuántos elementos debe renderizar.
    // Conclusión: La implementación es directa y adecuada para el propósito del adaptador.
    override fun getItemCount(): Int {
        return fileArray.size
    }
}
