package com.example.ecommerce.Pembeli

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ecommerce.Prevalent.Prevalent
import com.example.ecommerce.R
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ConfirmFinalOrderActivity : AppCompatActivity() {

    lateinit var nameEditText: EditText
    lateinit var phoneEditText: EditText
    lateinit var addressEditText: EditText
    lateinit var cityEditText: EditText
    lateinit var confrimOrderBtn: Button

    var totalAmount: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_final_order)

        totalAmount = intent.getStringExtra("Total Price")

        confrimOrderBtn = findViewById(R.id.confirm_final_order_btm)
        nameEditText = findViewById(R.id.shipment_name)
        phoneEditText = findViewById(R.id.shipment_phone_number)
        addressEditText = findViewById(R.id.shipment_address)
        cityEditText = findViewById(R.id.shipment_city)

        confrimOrderBtn.setOnClickListener { v ->

            Check()

        }



    }

    private fun Check() {

        if (TextUtils.isEmpty(nameEditText.text.toString())){
            Toast.makeText(this, "Tolong isi nama lengkap Anda",Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(phoneEditText.text.toString())){
            Toast.makeText(this, "Tolong isi nomor telepon Anda",Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(addressEditText.text.toString())){
            Toast.makeText(this, "Tolong isi alamat lengkap Anda",Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(cityEditText.text.toString())){
            Toast.makeText(this, "Tolong isi kota Anda",Toast.LENGTH_SHORT).show()
        }else{
            ConfirmOrder()
        }

    }

    private fun ConfirmOrder() {

        var saveCurrentTime : String
        var saveCurrentDate : String

        val calForDate = Calendar.getInstance()
        val currentDate = SimpleDateFormat("MMM dd, yyyy")
        saveCurrentDate = currentDate.format(calForDate.time)

        val currentTime = SimpleDateFormat("HH:mm:ss a")
        saveCurrentTime = currentTime.format(calForDate.time)

        val ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentOnlineUser!!.phone!!)

        val ordersMap:HashMap<String, Any> = HashMap()
        ordersMap.put("totalAmount", totalAmount!!)
        ordersMap.put("name", nameEditText.text.toString())
        ordersMap.put("phone", phoneEditText.text.toString())
        ordersMap.put("date", saveCurrentDate)
        ordersMap.put("time", saveCurrentTime)
        ordersMap.put("address", addressEditText.text.toString())
        ordersMap.put("city", cityEditText.text.toString())
        ordersMap.put("state", "shipped")

        ordersRef.updateChildren(ordersMap).addOnCompleteListener { task ->

            FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("User View")
                .child(Prevalent.CurrentOnlineUser!!.phone!!)
                .removeValue()
                .addOnCompleteListener { task ->
                    Toast.makeText(this, "Anda Berhasil Melakukan Order Product", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
        }

    }
}
