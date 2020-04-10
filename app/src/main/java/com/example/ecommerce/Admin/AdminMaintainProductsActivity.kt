package com.example.ecommerce.Admin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.ecommerce.Penjual.SellerProductCategoryActivity
import com.example.ecommerce.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class AdminMaintainProductsActivity : AppCompatActivity() {

    private lateinit var applyChangeBtn : Button
    private lateinit var deleteBtn : Button
    private lateinit var name : EditText
    private lateinit var price : EditText
    private lateinit var description : EditText
    private lateinit var address : EditText
    private lateinit var imageView : ImageView
    private var productsRef : DatabaseReference? = null

    private var productID : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_maintain_products)

        productID = intent.getStringExtra("pid")
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID!!)

        applyChangeBtn = findViewById(R.id.aplly_change_btn)
        name = findViewById(R.id.product_name_maintain)
        price = findViewById(R.id.product_price_maintain)
        description = findViewById(R.id.product_description_maintain)
        address = findViewById(R.id.product_alamat_maintain)
        imageView = findViewById(R.id.product_image_maintain)
        deleteBtn = findViewById(R.id.delete_product_btn)

        displaySpecificProductInfo()

        applyChangeBtn.setOnClickListener { v ->
            applyChanges()
        }

        deleteBtn.setOnClickListener { v ->
            deleteThisProduct()
        }

    }

    private fun deleteThisProduct() {
        productsRef!!.removeValue().addOnCompleteListener { task ->
            startActivity(Intent(this, SellerProductCategoryActivity::class.java))
            finish()
            
            Toast.makeText(this, "Iklan Produk Berhasil Dihapus", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyChanges() {

        val pName = name.text.toString()
        val pPrice = price.text.toString()
        val pDescription = description.text.toString()
        val pAddress = address.text.toString()

        if (pName.equals("")){
            Toast.makeText(this, "Isi Nama Product",Toast.LENGTH_SHORT).show()
        }else if (pPrice.equals("")){
            Toast.makeText(this, "Isi Harga Product",Toast.LENGTH_SHORT).show()
        }else if (pDescription.equals("")){
            Toast.makeText(this, "Isi Deskripsi Product",Toast.LENGTH_SHORT).show()
        }else if (pAddress.equals("")){
            Toast.makeText(this, "Isi Alamat Product",Toast.LENGTH_SHORT).show()
        }else{
            val productMap:HashMap<String, Any> = HashMap()
            productMap.put("pid", productID!!)
            productMap.put("description", pDescription)
            productMap.put("price", pPrice)
            productMap.put("pname", pName)
            productMap.put("address", pAddress)

            productsRef!!.updateChildren(productMap).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Data Produk Berhasil terUpdate", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, SellerProductCategoryActivity::class.java))
                }
            }
        }

    }

    private fun displaySpecificProductInfo() {

        productsRef!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val pName = p0.child("pname").getValue().toString()
                    val pPrice = p0.child("price").getValue().toString()
                    val pAddress = p0.child("address").getValue().toString()
                    val pDescription = p0.child("description").getValue().toString()
                    val pImage = p0.child("image").getValue().toString()

                    name.setText(pName)
                    price.setText(pPrice)
                    description.setText(pDescription)
                    address.setText(pAddress)
                    Picasso.get().load(pImage).into(imageView)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
}
