package com.uttab.aplicacionbien.Opciones_login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.uttab.aplicacionbien.MainActivity
import com.uttab.aplicacionbien.Registro_email
import com.uttab.aplicacionbien.databinding.ActivityLoginEmailBinding

class Login_email : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setMessage("Ingresando")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.BtnIngresar.setOnClickListener {
            validarInfo()
        }

        binding.TxtRegistrarme.setOnClickListener {
            startActivity(Intent(this@Login_email, Registro_email::class.java))
        }
    }

    private var email = ""
    private var password = ""

    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.EtEmail.error = "Ingrese email"
            binding.EtEmail.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EtEmail.error = "Email inválido"
            binding.EtEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.EtPassword.error = "Ingrese password"
            binding.EtPassword.requestFocus()
        } else {
            loginUsuario()
        }
    }

    private fun loginUsuario() {
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se pudo iniciar sesión debido a: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
