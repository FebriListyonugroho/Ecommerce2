package com.example.ecommerce.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.ecommerce.Interface.ItemClickListener
import com.example.ecommerce.R
import kotlinx.android.synthetic.main.cart_items_layout.view.*

class CartViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    override fun onClick(v: View) {
        itemClick!!.onClick(v, adapterPosition, false)
    }

    var txtProductName : TextView? = null
    var txtProductPrice : TextView? = null
    var txtProductQuantity : TextView? = null
    var itemClick: ItemClickListener? = null

    init {
        txtProductName = itemView.findViewById(R.id.cart_product_name)
        txtProductPrice = itemView.findViewById(R.id.cart_product_price)
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity)
    }

    fun setItemClickListner(itemClick: ItemClickListener){
        this.itemClick = itemClick
    }

}