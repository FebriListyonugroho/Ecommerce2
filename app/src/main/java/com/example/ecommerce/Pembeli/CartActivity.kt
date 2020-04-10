package com.example.ecommerce.Pembeli

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.ecommerce.Model.Cart
import com.example.ecommerce.Prevalent.Prevalent
import com.example.ecommerce.R
import com.example.ecommerce.ViewHolder.CartViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var NextProcessBtn: Button
    lateinit var txtTotalAmount: TextView
    lateinit var txtMsg1: TextView

    var overTotalPrice : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)


        recyclerView = findViewById(R.id.cart_list)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        txtMsg1 = findViewById(R.id.msg1)

        NextProcessBtn = findViewById(R.id.next_process_btn)
        txtTotalAmount = findViewById(R.id.total_price)

        NextProcessBtn.setOnClickListener { v ->

            txtTotalAmount.setText("Total Price : ${overTotalPrice.toString()}")

            val intent = Intent(this, ConfirmFinalOrderActivity::class.java)
            intent.putExtra("Total Price", overTotalPrice.toString())
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()

        CheckOrderState()

        val cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List")

        val options = FirebaseRecyclerOptions.Builder<Cart>()
            .setQuery(cartListRef.child("User View").child(Prevalent.CurrentOnlineUser!!.phone!!).child("Products"), Cart::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Cart, CartViewHolder>(options!!){

            override fun onBindViewHolder(holder: CartViewHolder, position: Int, model: Cart) {
                holder.txtProductName!!.setText(model.pname)
                holder.txtProductQuantity!!.setText("Jumlah : ${model.quantity}")
                holder.txtProductPrice!!.setText("Harga${model.price}")

                ///Perhitungan Total Pembayaran
                val oneTypeProductTPrice = ((Integer.valueOf(model.price))) * Integer.valueOf(model.quantity)
                overTotalPrice = overTotalPrice + oneTypeProductTPrice

                holder.itemView.setOnClickListener { v ->

                    var options = arrayOf<CharSequence>(
                        "Edit",
                        "Remove"
                    )
                    val builder = AlertDialog.Builder(this@CartActivity)
                    builder.setTitle("Cart Options:")

                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->

                        if (which == 0){
                            val intent = Intent(this@CartActivity, ProductDetailsActivity::class.java)
                            intent.putExtra("pid", model.pid)
                            startActivity(intent)
                        }
                        if (which == 1){
                            cartListRef.child("User View")
                                .child(Prevalent.CurrentOnlineUser!!.phone!!)
                                .child("Products")
                                .child(model.pid!!)
                                .removeValue()
                                .addOnCompleteListener {task ->
                                    if (task.isSuccessful){
                                        Toast.makeText(this@CartActivity, "Item Telah Brhasil Dihapus", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@CartActivity, HomeActivity::class.java))
                                    }
                                }
                        }

                    })
                    builder.show()

                }

            }

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CartViewHolder {
                val view = LayoutInflater.from(p0.context).inflate(R.layout.cart_items_layout, p0, false)
                val holder = CartViewHolder(view)
                return holder
            }
        }
        recyclerView.adapter = adapter
        adapter.startListening()
}
    private fun CheckOrderState(){

        val ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentOnlineUser!!.phone!!)

        ordersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    var shippingState = p0.child("state").getValue().toString()
                    var userName = p0.child("name").getValue().toString()

                    if (shippingState.equals("shipped")){
                        txtTotalAmount.setText("Dear : ${userName} \n pengiriman barang Berhasil")
                        recyclerView.visibility = View.GONE
                        txtMsg1.setText("Pesanan Anda telah dibuat. Tunngu sampai pesanan sampai ke Anda")

                        txtMsg1.visibility = View.VISIBLE
                        NextProcessBtn.visibility = View.GONE

                        Toast.makeText(this@CartActivity, "Anda dapat membeli lebih banyak produk, setelah Anda menerima pesanan Anda",Toast.LENGTH_SHORT).show()
                    }else if (shippingState.equals("not shipped")){

                        txtTotalAmount.setText("Status = Tidak dalam Pengiriman")
                        recyclerView.visibility = View.GONE

                        txtMsg1.visibility = View.VISIBLE
                        NextProcessBtn.visibility = View.GONE

                        Toast.makeText(this@CartActivity, "Anda dapat membeli lebih banyak produk, setelah Anda menerima pesanan Anda",Toast.LENGTH_SHORT).show()

                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }
}
