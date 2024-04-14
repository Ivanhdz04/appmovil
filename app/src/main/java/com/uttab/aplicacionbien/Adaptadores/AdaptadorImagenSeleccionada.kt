package com.uttab.aplicacionbien.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.uttab.aplicacionbien.Modelo.ModeloImageSeleccionada
import com.uttab.aplicacionbien.R
import com.uttab.aplicacionbien.databinding.ItemImagenesSeleccionadasBinding

class AdaptadorImagenSeleccionada(
    private val context : Context,
    private val imageneSelecArrayList : ArrayList<ModeloImageSeleccionada>
) : Adapter<AdaptadorImagenSeleccionada.HolderImagenSeleccionada>() {
    private lateinit var binding : ItemImagenesSeleccionadasBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagenSeleccionada {
        binding = ItemImagenesSeleccionadasBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderImagenSeleccionada(binding.root)
    }

    override fun getItemCount(): Int {
        return imageneSelecArrayList.size
    }

    override fun onBindViewHolder(holder: HolderImagenSeleccionada, position: Int) {
        val modelo = imageneSelecArrayList[position]

        val imagenUri = modelo.imagenUri

        try{
            Glide.with(context)
                .load(imagenUri)
                .placeholder(R.drawable.item_imagen)
                .into(holder.item_imagen)
        }catch (e:Exception){

        }

        holder.btn_cerrar.setOnClickListener {
            imageneSelecArrayList.remove(modelo)
            notifyDataSetChanged()
        }
    }

    inner class HolderImagenSeleccionada(itemView : View) : ViewHolder(itemView){
        var item_imagen = binding.itemImagen
        var btn_cerrar = binding.cerrarItem
    }


}