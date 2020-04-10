package com.example.ecommerce.Admin

import android.content.ComponentCallbacks2
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.ecommerce.Model.AdminOrders
import com.example.ecommerce.Prevalent.Prevalent
import com.example.ecommerce.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class AdminNewOrdersActivity : AppCompatActivity() {

    lateinit var orderList : RecyclerView
    lateinit var perantara : String
    private var orderRef : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_new_orders)

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")

        orderList = findViewById(R.id.orders_list)
        orderList.layoutManager = LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<AdminOrders>()
            .setQuery(orderRef!!, AdminOrders::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options){
            override fun onBindViewHolder(holder: AdminOrdersViewHolder, position: Int, model: AdminOrders) {
                holder.userName!!.setText("Name : ${model.name}")
                holder.userPhoneNumber!!.setText("Nomor Telepon : ${model.phone}")
                holder.userTotalPrice!!.setText("Total Harga : ${model.totalAmount}")
                holder.userDateTime!!.setText("Waktu Order : ${model.date} / ${model.time}")
                holder.userShippingAddress!!.setText("Alamat Lengkap : ${model.address}, kota ${model.city}")

                holder.ShowOrdersBtn!!.setOnClickListener { v ->

                    var uID : String = getRef(position).key!!

                    val intent = Intent(this@AdminNewOrdersActivity, AdminUserProductActivity::class.java)
                    intent.putExtra("uid", uID)
                    startActivity(intent)
                }

            }

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdminOrdersViewHolder {
                val view = LayoutInflater.from(p0.context).inflate(R.layout.orders_layout, p0, false)
                return AdminOrdersViewHolder(view)
            }
        }
        orderList.adapter = adapter
        adapter.startListening()

    }

    class AdminOrdersViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){

        var userName : TextView? = null
        var userPhoneNumber : TextView? = null
        var userTotalPrice : TextView? = null
        var userDateTime : TextView? = null
        var userShippingAddress : TextView? = null
        var ShowOrdersBtn : Button? = null

        init {
            userName = itemView.findViewById(R.id.order_user_name)
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number)
            userTotalPrice = itemView.findViewById(R.id.order_total_price)
            userDateTime = itemView.findViewById(R.id.order_date_time)
            userShippingAddress = itemView.findViewById(R.id.order_address_city)
            ShowOrdersBtn = itemView.findViewById(R.id.show_all_products_btn)
        }

    }

}
