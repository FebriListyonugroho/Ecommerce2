package com.example.ecommerce.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.ecommerce.Interface.ItemClickListener
import com.example.ecommerce.R

class ProductViewHolder(itemView:View):RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var imageView : ImageView? = null
    var txtproductName: TextView? = null
    var txtproductPrice: TextView? = null
    var txtaddress : TextView? = null
    var listener: ItemClickListener? = null

    init{
        imageView = itemView.findViewById(R.id.product_image)
        txtproductName = itemView.findViewById(R.id.product_name)
        txtproductPrice = itemView.findViewById(R.id.product_price)
        txtaddress = itemView.findViewById(R.id.product_address)

    }

    fun setItemClickListener(listener: ItemClickListener){
        this.listener = listener
    }

    override fun onClick(view:View) {
        listener!!.onClick(view, adapterPosition, false)
    }
}