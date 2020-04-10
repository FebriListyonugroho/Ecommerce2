package com.example.ecommerce.Pembeli

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.ecommerce.Admin.AdminHomeActivity
import com.example.ecommerce.Penjual.SellerProductCategoryActivity
import com.example.ecommerce.Model.Users
import com.example.ecommerce.Prevalent.Prevalent
import com.example.ecommerce.R
import com.google.firebase.database.*
import io.paperdb.Paper

class LoginActivity : AppCompatActivity() {

    private var InputNumber : EditText? = null
    private var InputPassword : EditText? = null
    private var LoginButton : Button? = null
    private var loadingBar : ProgressDialog? = null
    lateinit var chkBoxRememberMe : com.rey.material.widget.CheckBox

    private var AdminLink : TextView? = null
    private var NotAdminLink : TextView? = null
    private lateinit var ForgetPasswordLink : TextView


    private var parentDbName : String = "Users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        InputNumber = findViewById(R.id.login_phone_number_input)
        InputPassword = findViewById(R.id.login_password_input)
        LoginButton = findViewById(R.id.login_btn)
        AdminLink = findViewById(R.id.admin_panel_link)
        NotAdminLink = findViewById(R.id.not_admin_panel_link)
        ForgetPasswordLink = findViewById(R.id.forget_password_link)
        loadingBar = ProgressDialog(this)

        chkBoxRememberMe = findViewById(R.id.remember_me_chkb)
        Paper.init(this)

        LoginButton!!.setOnClickListener { v ->
            LoginUser()
        }

        ForgetPasswordLink.setOnClickListener { v ->
            val intent = Intent(this, ResetPasswordActivity::class.java)
            intent.putExtra("check", "login")
            startActivity(intent)
        }

        AdminLink!!.setOnClickListener { v ->
            LoginButton!!.setText("Login Admin")
            AdminLink!!.visibility = View.INVISIBLE
            NotAdminLink!!. visibility = View.VISIBLE
            parentDbName = "Admins"
        }

        NotAdminLink!!.setOnClickListener { v ->
            LoginButton!!.setText("Login")
            AdminLink!!.visibility = View.VISIBLE
            NotAdminLink!!. visibility = View.INVISIBLE
            parentDbName = "Users"
        }

    }

    private fun LoginUser() {
        val phone = InputNumber!!.text.toString()
        val password = InputPassword!!.text.toString()

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please write your phone number ...", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please write your password ...", Toast.LENGTH_SHORT).show()
        }else{
            loadingBar!!.setTitle("Login Account")
            loadingBar!!.setMessage("Please wait")
            loadingBar!!.setCanceledOnTouchOutside(false)
            loadingBar!!.show()

            AllowAccessToAccount(phone, password)
        }
    }

    private fun AllowAccessToAccount(phone: String, password: String) {

        if (chkBoxRememberMe!!.isChecked){
            Paper.book().write(Prevalent.UserPhoneKey, phone)
            Paper.book().write(Prevalent.UserPassword, password)
        }

        val RootRef : DatabaseReference
        var usersData : Users
        RootRef = FirebaseDatabase.getInstance().getReference()

        RootRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.child(parentDbName).child(phone).exists()){

                    usersData = p0.child(parentDbName).child(phone).getValue(Users::class.java)!!
                    if (usersData.phone.equals(phone)){
                        if (usersData.password.equals(password)){
                            if (parentDbName.equals("Admins")){
                                Toast.makeText(this@LoginActivity, "Welcome Admin, you are logged in Successfull ...", Toast.LENGTH_SHORT).show()
                                loadingBar!!.dismiss()
                                startActivity(Intent(this@LoginActivity, AdminHomeActivity::class.java))
                            }else if (parentDbName.equals("Users")){
                                Toast.makeText(this@LoginActivity, "logged in Successfull ...", Toast.LENGTH_SHORT).show()
                                loadingBar!!.dismiss()
                                Prevalent.CurrentOnlineUser = usersData
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            }
                        }else{
                            loadingBar!!.dismiss()
                            Toast.makeText(this@LoginActivity, "Password is incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }


                }else{
                    Toast.makeText(this@LoginActivity, "Account with this ${phone} number do not exists", Toast.LENGTH_SHORT).show()
                    loadingBar!!.dismiss()
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
}
