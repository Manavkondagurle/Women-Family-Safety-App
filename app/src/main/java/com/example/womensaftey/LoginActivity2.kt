package com.example.womensaftey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.womensaftey.data.SharedPref
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity2 : AppCompatActivity() {

    private val RC_SIGN_IN = 69
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    lateinit var edEmail: EditText
    lateinit var edPassword: EditText
    lateinit var btn: Button
    lateinit var tv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        auth = FirebaseAuth.getInstance()
        edEmail = findViewById(R.id.editTextLoginUsername)
        edPassword = findViewById(R.id.editTextLoginPassword)
        btn = findViewById(R.id.buttonLogin)

        btn.setOnClickListener(View.OnClickListener {
             fun onClick(view: View) {
                 startActivity(Intent(this@LoginActivity2, mainactivity::class.java))
                 finish()
                val email = edEmail.text.toString()
                val password = edPassword.text.toString()
                 if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                     if (password.isNotEmpty()) {
                         auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                             Toast.makeText(
                                 this@LoginActivity2,
                                 "Login Successful",
                                 Toast.LENGTH_SHORT
                             ).show()

                         }.addOnFailureListener {
                             Toast.makeText(
                                 this@LoginActivity2,
                                 "Login Failed",
                                 Toast.LENGTH_SHORT
                             ).show()
                         }
                     } else {
                         edPassword.error = "Password cannot be empty"
                     }
                 } else if (email.isEmpty()) {
                     edEmail.error = "Email cannot be empty"
                 } else {
                     edEmail.error = "Please enter valid email"
                 }
            }
        })
        btn.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity2,
                    mainactivity::class.java
                )
            )
        })
    }

    fun signIn(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("Error69", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Fire69", "signInWithCredential:success")

                    SharedPref.init(this)
                    SharedPref.putBoolean("isUserLoggedIn" , true)
                    val user = auth.currentUser

                    startActivity(Intent(this@LoginActivity2,mainactivity::class.java))
                } else {
                    Log.w("Fire69", "signInWithCredential:failure", task.exception)
                }
            }
    }
}