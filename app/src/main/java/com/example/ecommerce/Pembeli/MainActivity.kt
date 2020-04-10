package com.example.ecommerce.Pembeli

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.example.ecommerce.Model.Users
import com.example.ecommerce.Prevalent.Prevalent
import com.google.firebase.database.*
import io.paperdb.Paper
import android.view.WindowManager
import android.os.Build
import android.widget.TextView
import com.example.ecommerce.Penjual.SellerHomeActivity
import com.example.ecommerce.Penjual.SellerRegistrationActivity
import com.example.ecommerce.R
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private var joinNowButton : Button? = null
    private var loginButton : Button? = null
    private var loadingBar : ProgressDialog? = null
    private lateinit var sellerBegin : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        joinNowButton = findViewById(R.id.main_join_now_btn)
        loginButton = findViewById(R.id.main_login_btn)
        sellerBegin = findViewById(R.id.seller_begin)
        loadingBar = ProgressDialog(this)

        Paper.init(this)

        sellerBegin.setOnClickListener { v ->
            startActivity(Intent(this, SellerRegistrationActivity::class.java))
        }

        loginButton?.setOnClickListener { v ->
            startActivity(Intent(this, LoginActivity::class.java))
        }

        joinNowButton?.setOnClickListener { v ->
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val UserPhoneKey = Paper.book().read<String>(Prevalent.UserPhoneKey)
        val UserPasswordKey = Paper.book().read<String>(Prevalent.UserPassword)

        if (UserPhoneKey != "" && UserPasswordKey != ""){
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){
                AllowAccess(UserPhoneKey, UserPasswordKey)

                loadingBar!!.setTitle("Already Logged in")
                loadingBar!!.setMessage("Please wait ...")
                loadingBar!!.setCanceledOnTouchOutside(false)
                loadingBar!!.show()

            }
        }

    }

    override fun onStart() {
        super.onStart()

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null){
            val intent = Intent(this, SellerHomeActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun AllowAccess(phone : String?, password: String?) {

        val RootRef : DatabaseReference
        var usersData : Users
        RootRef = FirebaseDatabase.getInstance().getReference()

        RootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.child("Users").child(phone!!).exists()){

                    usersData = p0.child("Users").child(phone).getValue(Users::class.java)!!
                    if (usersData.phone.equals(phone!!)){
                        if (usersData.password.equals(password)){
                            Toast.makeText(this@MainActivity, "Please wait, you are already logged in ...", Toast.LENGTH_SHORT).show()
                            loadingBar!!.dismiss()
                            Prevalent.CurrentOnlineUser = usersData
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        }else{
                            loadingBar!!.dismiss()
                            Toast.makeText(this@MainActivity, "Password is incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }


                }else{
                    Toast.makeText(this@MainActivity, "Account with this ${phone} number do not exists", Toast.LENGTH_SHORT).show()
                    loadingBar!!.dismiss()
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
