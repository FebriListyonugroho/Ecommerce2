package com.example.ecommerce.Pembeli

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.ecommerce.Model.Products
import com.example.ecommerce.R
import com.example.ecommerce.ViewHolder.ProductViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class SearchProductActivity : AppCompatActivity() {

    lateinit var SearchBtn : Button
    lateinit var inputText : EditText
    lateinit var searchList : RecyclerView
    var SearchInput : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

        inputText = findViewById(R.id.search_product_name)
        SearchBtn = findViewById(R.id.search_btn)
        searchList = findViewById(R.id.search_list)
        searchList.layoutManager = LinearLayoutManager(this)

        SearchBtn.setOnClickListener { v ->
            SearchInput = inputText.text.toString()
            onStart()
        }

    }

    override fun onStart() {
        super.onStart()

        val reference = FirebaseDatabase.getInstance().getReference().child("Products")

        val options = FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(reference.orderByChild("pname").startAt(SearchInput), Products::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Products, ProductViewHolder>(options){
            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Products) {
                holder.txtproductName!!.setText(model.pname)
                holder.txtaddress!!.setText(model.address)
                holder.txtproductPrice!!.setText("Rp. ${model.price}")

                Picasso.get().load(model.image).into(holder.imageView)

                holder.itemView.setOnClickListener { v ->
                    val intent = Intent(this@SearchProductActivity, ProductDetailsActivity::class.java)
                    intent.putExtra("pid", model.pid)
                    startActivity(intent)
                }
            }

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProductViewHolder {
                val view = LayoutInflater.from(p0.context).inflate(R.layout.product_items_layout, p0, false)
                val holder = ProductViewHolder(view)
                return holder
            }
        }
        searchList.adapter = adapter
        adapter.startListening()

    }
}
