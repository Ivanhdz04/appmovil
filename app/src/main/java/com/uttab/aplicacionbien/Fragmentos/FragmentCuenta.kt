package com.uttab.aplicacionbien.Fragmentos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uttab.aplicacionbien.Constantes
import com.uttab.aplicacionbien.EditarPerfil
import com.uttab.aplicacionbien.OpcionesLogin
import com.uttab.aplicacionbien.databinding.FragmentCuentaBinding
import com.uttab.aplicacionbien.R


class FragmentCuenta : Fragment() {

    private var _binding: FragmentCuentaBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext : Context

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCuentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        leerInfo()

        binding.BtnEditarPerfil.setOnClickListener {
            startActivity(Intent(mContext, EditarPerfil::class.java))
        }

        binding.BtnCerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), OpcionesLogin::class.java))
            activity?.finishAffinity()
        }
    }


    private fun leerInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        val addValueEventListener = ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    val imagen = "${snapshot.child("urlImagenPerfil").value}"
                    val f_nac = "${snapshot.child("fecha_nac").value}"
                    var tiempo = "${snapshot.child("tiempo").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val codTelefono = "${snapshot.child("codigoTelefono").value}"
                    val proveedor = "${snapshot.child("proveedor").value}"

                    val cod_tel = codTelefono+telefono

                    if (tiempo == "null"){
                        tiempo = "0"
                    }
                    val for_tiempo = Constantes.obtenerFecha(tiempo.toLong())
                    //Seteamos informacion
                    binding.TvEmail.text = email
                    binding.TvNombres.text = nombres
                    binding.TvNacimiento.text = f_nac
                    binding.TvTelefono.text = cod_tel
                    binding.TvMiembro.text = for_tiempo

                    //Seteamos la imagen
                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.IvPerfil)
                    }catch (e:Exception){
                        Toast.makeText(
                            mContext,
                            "${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (proveedor == "Email"){
                        val esVerificado = firebaseAuth.currentUser!!.isEmailVerified
                        if (esVerificado){
                            binding.TvEstadoCuenta.text = "Verificado"
                        }else{
                            binding.TvEstadoCuenta.text = "No verificado"
                        }
                    }else{
                        binding.TvEstadoCuenta.text = "Verificado"
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}
