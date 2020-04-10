package com.example.ecommerce.Admin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ecommerce.Model.Cart
import com.example.ecommerce.R
import com.example.ecommerce.ViewHolder.CartViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminUserProductActivity : AppCompatActivity() {

    lateinit var productsList : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    var cartListRef : DatabaseReference? = null

    var userID : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_user_product)

        userID = intent.getStringExtra("uid")

        productsList = findViewById(R.id.products_list)
        productsList.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        productsList.layoutManager = layoutManager

        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
            .child("Admin View").child(userID!!).child("Products")

    }

    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<Cart>()
            .setQuery(cartListRef!!, Cart::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Cart, CartViewHolder>(options){
            override fun onBindViewHolder(holder: CartViewHolder, position: Int, model: Cart) {
                holder.txtProductName!!.setText(model.pname)
                holder.txtProductQuantity!!.setText("Jumlah : ${model.quantity}")
                holder.txtProductPrice!!.setText("Harga${model.price}")
            }

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CartViewHolder {
                val view = LayoutInflater.from(p0.context).inflate(R.layout.cart_items_layout, p0, false)
                val holder = CartViewHolder(view)
                return holder
            }
        }
        productsList.adapter = adapter
        adapter.startListening()
    }

}
