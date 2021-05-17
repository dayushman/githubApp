package com.example.githubapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.githubapp.R
import com.example.githubapp.databinding.GithubLoginBinding
import com.example.githubapp.ui.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class LoginActivity : AppCompatActivity() {

    lateinit var fAuth:FirebaseAuth
    val TAG = "LOGIN"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.github_login)

        val email = findViewById<EditText>(R.id.et_email)
        fAuth = FirebaseAuth.getInstance()
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            if(email.text.isNullOrEmpty())
                Toast.makeText(this,"Enter Email!",Toast.LENGTH_LONG).show()
            else{
                val scopes = ArrayList<String>()
                scopes.add("read:user")
                signInWithProvider(
                    OAuthProvider.newBuilder("github.com")
                        .addCustomParameter("login",email.text.toString())
                        .setScopes(scopes).build()
                )

            }
        }

    }

    private fun signInWithProvider(provider: OAuthProvider) {
        val pendingAuthTask = fAuth.pendingAuthResult
        if (pendingAuthTask!=null){
            pendingAuthTask.addOnSuccessListener {
                Toast.makeText(this,"User Exists!",Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener {
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                }
        }
        else{
            fAuth.startActivityForSignInWithProvider(this,provider)
                .addOnSuccessListener { authResult ->
                    val currentUser = authResult.user
                    val profile = authResult.additionalUserInfo?.profile
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("email",currentUser?.email.toString())
                    intent.putExtra("name",currentUser?.displayName.toString())
                    intent.putExtra("icon",currentUser?.photoUrl.toString())
                    intent.putExtra("id",currentUser?.providerId)
                    startActivity(intent)
                    Log.e(TAG, "signInWithProvider:${currentUser?.photoUrl}")
                }
                .addOnFailureListener {
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                }
        }
    }
}