package com.uttab.aplicacionbien

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.uttab.aplicacionbien.Opciones_login.Login_email
import com.uttab.aplicacionbien.databinding.ActivityOpcionesLoginBinding
import java.lang.Exception

class OpcionesLogin : AppCompatActivity() {

    private lateinit var binding: ActivityOpcionesLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere porfavor")
        progressDialog.setCanceledOnTouchOutside(false)


        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)



        binding.IngresarEmail.setOnClickListener {
            startActivity(Intent(this@OpcionesLogin, Login_email::class.java))
        }
        binding.IngresarGoogle.setOnClickListener {
            googleLogin()

        }
    }

    private fun googleLogin() {
        val googleSingIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSingIntent)
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                autenticacionGoogle(cuenta.idToken)
            } catch (e: Exception) {
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun autenticacionGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { resultadoAuth ->
                if (resultadoAuth.additionalUserInfo!!.isNewUser) {
                    llenarInfoBD()

                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun llenarInfoBD() {
        progressDialog.setMessage("Guardando información")

        val tiempo = Constantes.obtenerTiempoDis()
        val emailUsuario = firebaseAuth.currentUser!!.email
        val uidUsuario = firebaseAuth.uid
        val nombreUsuario = firebaseAuth.currentUser?.displayName

        val hashMap = HashMap<String, Any>()
        hashMap["nombres"] = "${nombreUsuario}"
        hashMap["codigoTelefono"] = ""
        hashMap["telefono"] = ""
        hashMap["urlImagenPerfil"] = ""
        hashMap["proveedor"] = "Google"
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
                Toast.makeText(
                    this,
                    "No se guardó la información: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun comprobarSesion() {
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity();
        }
    }
}


