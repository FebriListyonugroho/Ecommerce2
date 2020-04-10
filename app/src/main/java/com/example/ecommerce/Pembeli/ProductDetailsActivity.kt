package com.example.ecommerce.Pembeli

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.ecommerce.Model.Products
import com.example.ecommerce.Prevalent.Prevalent
import com.example.ecommerce.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailsActivity : AppCompatActivity() {

    var productImage : ImageView? = null
    var ProductRef: DatabaseReference? = null
    var state : String? = "Normal"

    lateinit var numberButton : ElegantNumberButton
    lateinit var productPrice : TextView
    lateinit var productDescription : TextView
    lateinit var addToCartButton: Button
    lateinit var productName : TextView
    lateinit var productID : String
    lateinit var products: Products



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        productID = intent.getStringExtra("pid")

        addToCartButton = findViewById(R.id.pd_add_to_cart_button)
        numberButton = findViewById(R.id.number_btn)
        productImage = findViewById(R.id.product_image_details)
        productName = findViewById(R.id.product_name_details)
        productDescription = findViewById(R.id.product_description_details)
        productPrice = findViewById(R.id.product_price_details)

        getProductDetails(productID)

        addToCartButton.setOnClickListener { v ->



            if (state.equals("Order Placed") || state!!.equals("Order Shipped")){
                Toast.makeText(this, "Anda dapat menambahkan pembelian lebih banyak produk, setelah Anda memesan dikirim atau dikonfirmasi", Toast.LENGTH_SHORT).show()
            }else{
                addingToCartList()
            }

        }

    }


    override fun onStart() {
        super.onStart()
        CheckOrderState()
    }

    private fun addingToCartList() {

        var saveCurrentTime : String
        var saveCurrentDate : String

        val calForDate = Calendar.getInstance()
        val currentDate = SimpleDateFormat("MMM dd, yyyy")
        saveCurrentDate = currentDate.format(calForDate.time)

        val currentTime = SimpleDateFormat("HH:mm:ss a")
        saveCurrentTime = currentDate.format(calForDate.time)


        val cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List")

        val cartMap:HashMap<String, Any> = HashMap()
        cartMap.put("pid", productID)
        cartMap.put("pname", productName.text.toString())
        cartMap.put("price", productPrice.text.toString())
        cartMap.put("date", saveCurrentDate)
        cartMap.put("time", saveCurrentTime)
        cartMap.put("quantity", numberButton.number)
        cartMap.put("discount", "")

        cartListRef.child("User View").child(Prevalent.CurrentOnlineUser!!.phone!!).child("Products")
            .child(productID).updateChildren(cartMap).addOnCompleteListener { task ->

                if (task.isSuccessful){

                    cartListRef.child("Admin View").child(Prevalent.CurrentOnlineUser!!.phone!!).child("Products")
                        .child(productID).updateChildren(cartMap).addOnCompleteListener { task ->

                            if (task.isSuccessful){

                                Toast.makeText(this, "Berhasil Masuk Keranjang", Toast.LENGTH_SHORT).show()

                                startActivity(Intent(this, HomeActivity::class.java))

                            }

                        }

                }

            }

    }

    private fun getProductDetails(productID: String) {
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products")

        ProductRef!!.child(productID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    products = p0.getValue(Products::class.java)!!

                    productName.setText(products.pname)
                    productPrice.setText("Rp. ${products.price}")
                    productDescription.setText(products.description)
                    Picasso.get().load(products.image).into(productImage)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun CheckOrderState(){

        val ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentOnlineUser!!.phone!!)

        ordersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    var shippingState = p0.child("state").getValue().toString()

                    if (shippingState.equals("shipped")){

                        state = "Order Shipped"

                    }else if (shippingState.equals("not shipped")){

                        state = "Order Placed"

                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

}
