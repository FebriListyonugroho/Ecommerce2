package com.example.ecommerce.Penjual

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ecommerce.R
import com.google.firebase.auth.FirebaseAuth

class SellerLoginActivity : AppCompatActivity() {

    private lateinit var registerSellerBtn : Button
    private lateinit var emailInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var loadingBar : ProgressDialog
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_login)

        loadingBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()

        emailInput = findViewById(R.id.seller_login_email)
        passwordInput = findViewById(R.id.seller_login_password)
        registerSellerBtn = findViewById(R.id.seller_login_btn)

        registerSellerBtn.setOnClickListener { v ->
            LoginSeller()
        }
    }

    private fun LoginSeller() {

        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        if (!email.equals("") && !password.equals("")) {

            loadingBar!!.setTitle("Masuk Ke Akun Penjual")
            loadingBar!!.setMessage("Harap Tunggu")
            loadingBar!!.setCanceledOnTouchOutside(false)
            loadingBar!!.show()

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful){

                    loadingBar.dismiss()
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, SellerHomeActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)

                }else{
                    loadingBar.dismiss()
                    Toast.makeText(this, "${task.exception}", Toast.LENGTH_SHORT).show()
                }

            }

        }else{
            Toast.makeText(this, "Harap isi Data Form Login", Toast.LENGTH_SHORT).show()
        }
    }
}
