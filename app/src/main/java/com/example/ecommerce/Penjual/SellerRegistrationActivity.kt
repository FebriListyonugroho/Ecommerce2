package com.example.ecommerce.Penjual

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.ecommerce.Pembeli.MainActivity
import com.example.ecommerce.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SellerRegistrationActivity : AppCompatActivity() {

    private lateinit var sellerLoginBegin : Button
    private lateinit var nameInput : EditText
    private lateinit var phoneInput : EditText
    private lateinit var emailInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var addressInput : EditText
    private lateinit var registerButton: Button
    private var mAuth : FirebaseAuth? = null
    private var loadingBar : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_registration)

        mAuth = FirebaseAuth.getInstance()

        sellerLoginBegin = findViewById(R.id.seller_already_have_account_btn)
        nameInput = findViewById(R.id.seller_name)
        phoneInput = findViewById(R.id.seller_phone)
        emailInput = findViewById(R.id.seller_email)
        passwordInput = findViewById(R.id.seller_password)
        addressInput = findViewById(R.id.seller_address)
        registerButton = findViewById(R.id.seller_register_btn)
        loadingBar = ProgressDialog(this)

        sellerLoginBegin.setOnClickListener { v ->
            startActivity(Intent(this, SellerLoginActivity::class.java))
        }

        registerButton.setOnClickListener { v ->
            registerSeller()
        }

    }

    private fun registerSeller() {

        val name = nameInput.text.toString()
        val phone = phoneInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val address = addressInput.text.toString()

        if (!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals("")){

            loadingBar!!.setTitle("Membuat Akun Penjual")
            loadingBar!!.setMessage("Harap Tunggu")
            loadingBar!!.setCanceledOnTouchOutside(false)
            loadingBar!!.show()

            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener (this){ task ->

                if (task.isSuccessful){
                    val rootRef = FirebaseDatabase.getInstance().getReference()

                    val sid = mAuth!!.currentUser!!.uid

                    val sellerMap:HashMap<String, Any> = HashMap()
                    sellerMap.put("sid", sid)
                    sellerMap.put("phone", phone)
                    sellerMap.put("email", email)
                    sellerMap.put("address", address)
                    sellerMap.put("name", name)

                    rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                        .addOnCompleteListener { task ->
                            loadingBar!!.dismiss()
                            Toast.makeText(this, "Berhasil Melakukan Registrasi Penjual", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, SellerHomeActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                }else{
                    loadingBar!!.dismiss()
                    Toast.makeText(this,"${task.exception}",Toast.LENGTH_SHORT).show()
                }

            }

        }else{
            Toast.makeText(this, "Harap isi Form yang ada", Toast.LENGTH_SHORT).show()
        }

    }
}
