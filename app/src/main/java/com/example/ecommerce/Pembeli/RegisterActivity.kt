package com.example.ecommerce.Pembeli

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ecommerce.R
import com.google.firebase.database.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    private var CreateAccountButton : Button? = null
    private var InputName : EditText? = null
    private var InputPhoneNumber : EditText? = null
    private var InputPassword : EditText? = null
    private var loadingBar : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        CreateAccountButton = findViewById(R.id.register_btn)
        InputName = findViewById(R.id.register_username_input)
        InputPhoneNumber = findViewById(R.id.register_phone_number_input)
        InputPassword = findViewById(R.id.register_password_input)

        loadingBar = ProgressDialog(this)

        CreateAccountButton?.setOnClickListener { v ->
            CreateAccount()
        }

    }

    private fun CreateAccount(){

        val name = InputName!!.text.toString()
        val phone = InputPhoneNumber!!.text.toString()
        val password = InputPassword!!.text.toString()

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Harap Isi Nama Anda ...", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Harap Isi Nomor Telpon Anda ...", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Harap Isi Password ...", Toast.LENGTH_SHORT).show()
        }else{
            loadingBar!!.setTitle("Membuat Akun")
            loadingBar!!.setMessage("Harap Tunggu")
            loadingBar!!.setCanceledOnTouchOutside(false)
            loadingBar!!.show()

            ValidatephoneNumber(name, phone, password)

        }

    }

    private fun ValidatephoneNumber(name: String, phone: String, password: String) {

        val RootRef : DatabaseReference
        RootRef = FirebaseDatabase.getInstance().getReference()

        RootRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                if (!(p0.child("Users").child(phone).exists())){

                    val userdataMap:HashMap<String, Any> = HashMap()
                    userdataMap.put("phone", phone)
                    userdataMap.put("password", password)
                    userdataMap.put("name", name)
                    userdataMap.put("image", "https://firebasestorage.googleapis.com/v0/b/ecommerce-a3368.appspot.com/o/Profile%20pictures%2Fprofile.png?alt=media&token=0f39d4f8-c18b-48c2-a538-aaa5a14189c7")

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                        .addOnCompleteListener{task ->
                            if (task.isSuccessful){
                                Toast.makeText(this@RegisterActivity, "Account Berhasil Dibuat", Toast.LENGTH_SHORT).show()
                                loadingBar!!.dismiss()

                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            }else{
                                loadingBar!!.dismiss()
                                Toast.makeText(this@RegisterActivity, "Cek Koneksi", Toast.LENGTH_SHORT).show()

                            }
                        }
                }else{
                    Toast.makeText(this@RegisterActivity, "Nomor ${phone} Sudah Terdaftar", Toast.LENGTH_SHORT).show()
                    loadingBar!!.dismiss()
                    Toast.makeText(this@RegisterActivity, "Nomor Telpon Telah Digunakan", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

    }

}
