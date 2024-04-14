package com.uttab.aplicacionbien

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.uttab.aplicacionbien.databinding.ActivityRegistroEmailBinding

class Registro_email : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroEmailBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere porfavor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.BtnRegistrar.setOnClickListener {
            validarInfo()
        }
    }


    private var email = ""
    private var password = ""
    private var r_password = ""
    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()
        r_password = binding.TxtEtRPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmail.error = "Email inválido"
            binding.EtEmail.requestFocus()
        }
        else if (email.isEmpty()){
            binding.EtEmail.error = "Ingrese email"
            binding.EtEmail.requestFocus()
        }
        else if (password.isEmpty()){
            binding.EtPassword.error = "Ingrese password"
            binding.EtPassword.requestFocus()
        }
        else if (r_password.isEmpty()){
            binding.TxtEtRPassword.error = "Repita el password"
            binding.TxtEtRPassword.requestFocus()
        }
        else if (password != r_password){
            binding.EtPassword.error = "No coinciden"
            binding.EtPassword.requestFocus()
        }
        else{
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                firebaseAuth.currentUser?.let { user ->
                    llenarInfoBD(user.uid)
                } ?: run {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error al obtener el usuario registrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "No se registró el usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun llenarInfoBD(uidUsuario: String) {
        progressDialog.setMessage("Guardando información")

        val tiempo = Constantes.obtenerTiempoDis()
        val emailUsuario = firebaseAuth.currentUser!!.email
        val uidUsuario = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
            hashMap["nombres"] = ""
            hashMap["codigoTelefono"] = ""
            hashMap["telefono"] = ""
            hashMap["urlImagenPerfil"] = ""
            hashMap["proveedor"] = "Email"
            hashMap["escribiendo"] = ""
            hashMap["tiempo"] = tiempo
            hashMap["online"] = "true"
            hashMap["email"] = "${emailUsuario}"
            hashMap["uid"] = "${uidUsuario}"
            hashMap["fecha_nac"] = ""

            val ref = FirebaseDatabase.getInstance().getReference("Usuarios").child(uidUsuario!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "No se guardó la información: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
