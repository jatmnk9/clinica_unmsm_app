import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectocita.R
import com.example.proyectocita.database.Cita

class CitaAdapter(
    private val citas: List<Cita>,
    private val onCitaSelected: (Cita) -> Unit
) : RecyclerView.Adapter<CitaAdapter.CitaViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]
        holder.bind(cita)

        // Configura el estado seleccionado
        holder.itemView.isSelected = position == selectedPosition

        holder.itemView.setOnClickListener {
            // Actualiza la posición seleccionada y refresca las vistas
            val previousPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(previousPosition) // Actualiza el ítem previamente seleccionado
            notifyItemChanged(selectedPosition) // Actualiza el nuevo ítem seleccionado

            onCitaSelected(cita) // Notifica la selección
        }
    }

    override fun getItemCount() = citas.size

    class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(cita: Cita) {
            // Configurar los datos de la cita en las vistas
            itemView.findViewById<TextView>(R.id.tvFecha).text = cita.fecha
            itemView.findViewById<TextView>(R.id.tvHora).text = cita.hora
            itemView.findViewById<TextView>(R.id.tvDoctor).text = cita.nombreDoctor
            itemView.findViewById<TextView>(R.id.tvEspecialidad).text = cita.especialidad
        }
    }
}
