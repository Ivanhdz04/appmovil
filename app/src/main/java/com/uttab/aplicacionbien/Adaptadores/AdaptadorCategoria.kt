package com.uttab.aplicacionbien.Adaptadores

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.uttab.aplicacionbien.Modelo.ModeloCategoria
import com.uttab.aplicacionbien.RvListenerCategoria
import com.uttab.aplicacionbien.databinding.ActivitySeleccionarUbicacionBinding
import com.uttab.aplicacionbien.databinding.ItemCategoriaBinding
import com.uttab.aplicacionbien.databinding.ItemCategoriaInicioBinding
import kotlin.random.Random

class AdaptadorCategoria(
    private val context: Context,
    private val categoriaArrayList: ArrayList<ModeloCategoria>,
    private val rvListenerCategoria: RvListenerCategoria
): Adapter<AdaptadorCategoria.HolderCategoria>(){

    private lateinit var binding: ItemCategoriaInicioBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria {
        binding = ItemCategoriaInicioBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategoria(binding.root)
    }

    override fun getItemCount(): Int {
        return categoriaArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria, position: Int) {
        val modeloCategoria = categoriaArrayList[position]

        val icono = modeloCategoria.icon
        val categoria = modeloCategoria.categoria

        val random = java.util.Random()
        val color = Color.argb(
            255,
            random.nextInt(255),
            random.nextInt(255),
            random.nextInt(255)
        )

        holder.categoriaIconoIv.setImageResource(icono)
        holder.categoriaTv.text = categoria
        holder.categoriaIconoIv.setBackgroundColor(color)

        holder.itemView.setOnClickListener {
            rvListenerCategoria.onCategoriaClick(modeloCategoria)

        }
    }


    inner class HolderCategoria(itemView: View) : ViewHolder(itemView){
        var categoriaIconoIv = binding.categoriaIconoIv
        var categoriaTv = binding.TvCategoria
    }


}