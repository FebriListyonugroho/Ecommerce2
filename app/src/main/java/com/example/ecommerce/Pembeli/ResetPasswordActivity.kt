package com.example.ecommerce.Pembeli

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.ecommerce.Prevalent.Prevalent
import com.example.ecommerce.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResetPasswordActivity : AppCompatActivity() {

    private var check : String? = ""
    private lateinit var pageTitle : TextView
    private lateinit var titleQuestions : TextView
    private lateinit var phoneNumber : EditText
    private lateinit var question1 : EditText
    private lateinit var question2 :EditText
    private lateinit var verifyButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        check = intent.getStringExtra("check")

        pageTitle = findViewById(R.id.page_title)
        titleQuestions = findViewById(R.id.title_question)
        phoneNumber = findViewById(R.id.find_phone_number)
        question1 = findViewById(R.id.question_1)
        question2 = findViewById(R.id.question_2)
        verifyButton = findViewById(R.id.verify_btn)

    }

    override fun onStart() {
        super.onStart()

        phoneNumber.visibility = View.GONE

        if (check.equals("settings")){

            pageTitle.setText("Atur Pertanyaan")
            titleQuestions.setText("Harap Atur pertanyaan keamanan anda berikut ?")
            verifyButton.setText("Atur Keamanan")

            displayPreviousAnswers()

            verifyButton.setOnClickListener { v ->
                setAnswers()
            }

        }else if (check.equals("login")){
            phoneNumber.visibility = View.VISIBLE

            verifyButton.setOnClickListener { v ->
                verifyUser()
            }

        }

    }

    private fun setAnswers(){

        val answer1 = question1.text.toString().toLowerCase()
        val answer2 = question2.text.toString().toLowerCase()

        if (question1.equals("") && question2.equals("")){
            Toast.makeText(this, "Harap jawab pertanyaan berikut", Toast.LENGTH_SHORT).show()
        }else{
            val ref = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(Prevalent.CurrentOnlineUser!!.phone!!)

            val userdataMap:HashMap<String, Any> = HashMap()
            userdataMap.put("answer1", answer1)
            userdataMap.put("answer2", answer2)

            ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener { task ->

                if (task.isSuccessful){
                    Toast.makeText(this, "Anda Berhasil memiliki jawaban untuk pertanyaan keamanan", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, HomeActivity::class.java))
                }

            }
        }
    }

    private fun displayPreviousAnswers(){

        val ref = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(Prevalent.CurrentOnlineUser!!.phone!!)

        ref.child("Security Questions").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){

                    val ans1 = p0.child("answer1").getValue().toString()
                    val ans2 = p0.child("answer2").getValue().toString()

                    question1.setText(ans1)
                    question2.setText(ans2)

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun verifyUser(){

        val phone = phoneNumber.text.toString()
        val answer1 = question1.text.toString().toLowerCase()
        val answer2 = question2.text.toString().toLowerCase()

        if (!phone.equals("") && !answer1.equals("") && !answer2.equals("")){
            val ref = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(phone)

            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()){
                        val mPhone = p0.child("phone").getValue().toString()
                        if (p0.hasChild("Security Questions")){
                            val ans1 = p0.child("Security Questions").child("answer1").getValue().toString()
                            val ans2 = p0.child("Security Questions").child("answer2").getValue().toString()

                            if (!ans1.equals(answer1)){
                                Toast.makeText(this@ResetPasswordActivity, "Jawaban Pertama Anda Salah !", Toast.LENGTH_SHORT).show()
                            }else if (!ans2.equals(answer2)){
                                Toast.makeText(this@ResetPasswordActivity, "Jawaban Kedua Anda Salah !", Toast.LENGTH_SHORT).show()
                            }else{
                                val builder = AlertDialog.Builder(this@ResetPasswordActivity)
                                builder.setTitle("Password Baru")

                                val newPassword = EditText(this@ResetPasswordActivity)
                                newPassword.hint = "Isi Password Anda"

                                builder.setView(newPassword)

                                builder.setPositiveButton("ubah", DialogInterface.OnClickListener { dialog, which ->
                                    if (!newPassword.text.toString().equals("")){
                                        ref.child("password")
                                            .setValue(newPassword.text.toString())
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful){
                                                    Toast.makeText(this@ResetPasswordActivity, "Password Berhasil Diubah", Toast.LENGTH_SHORT).show()
                                                    startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                                                }
                                            }
                                    }

                                })
                                builder.setNegativeButton("Batal", DialogInterface.OnClickListener { dialog, which ->
                                    dialog.cancel()
                                })
                                builder.show()
                            }
                        }
                        else{
                            Toast.makeText(this@ResetPasswordActivity, "Anda Belum set Keamanan",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this@ResetPasswordActivity, "Nomor Telepon Belum Terdaftar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }else{
            Toast.makeText(this, "Harap Isi Form Pertanyaan !",Toast.LENGTH_SHORT).show()
        }
    }
}
