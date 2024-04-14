package com.uttab.aplicacionbien

import android.content.Context
import android.text.format.DateFormat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

object Constantes {

    const val anuncio_disponible = "Disponible"
    const val anuncio_vendido = "Vendido"

    val categorias = arrayOf(
        "Todos",
        "Mobiles",
        "Ordenadores/Laptos",
        "Electronica y electrodomesticos",
        "Vehiculos",
        "Consolas y videojuegos",
        "Hogar y muebles",
        "Belleza y cuidado personal",
        "Libros",
        "Deportes",
        "Juguetes y figuras",
        "Mascotas"
    )

    val categoriasIcono = arrayOf(
        R.drawable.ic_categoria_todos,
        R.drawable.ic_categoria_mobiles,
        R.drawable.ic_categoria_laptops,
        R.drawable.ic_categoria_electrodosmeticos,
        R.drawable.ic_categoria_vehiculos,
        R.drawable.ic_categoria_consola,
        R.drawable.ic_categoria_muebles,
        R.drawable.ic_categoria_belleza,
        R.drawable.ic_categoria_libros,
        R.drawable.ic_categoria_deportes,
        R.drawable.ic_categoria_juguetes,
        R.drawable.ic_categoria_mascotas


    )

    val condiciones = arrayOf(
        "Nuevo",
        "Usado",
        "Renovado"
    )


    fun obtenerTiempoDis() : Long{
        return System.currentTimeMillis()
    }
    fun obtenerFecha(tiempo : Long) : String {
        val calendario = Calendar.getInstance(Locale.ENGLISH)
        calendario.timeInMillis = tiempo

        return DateFormat.format("dd/MM/yyyy", calendario).toString()
    }


    fun agregarAnuncioFav (context : Context, idAnuncio : String){
        val firebaseAuth = FirebaseAuth.getInstance()
        val tiempo = Constantes.obtenerTiempoDis()

        val hashMap = HashMap<String, Any>()
        hashMap["idAnuncio"] = idAnuncio
        hashMap["tiempo"] = tiempo

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idAnuncio)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(context,
                    "Anuncio agregado a favoritos",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context,
                    "${e.message}",
                    Toast.LENGTH_SHORT).show()

            }

    }


    fun eliminarAnuncioFav (context : Context, idAnuncio : String)  {
        val firebaseAuth = FirebaseAuth.getInstance()

        val ref =  FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(idAnuncio)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context,
                    "Anuncio eliminado de favoritos",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context,
                    "${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }












}