package com.example.ecommerce.Model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class AdminOrders (var name : String? = "", var phone : String? = "", var address : String? = "", var city : String? = "", var state : String? = "", var date : String? = "", var time : String? = "", var totalAmount : String? = "")