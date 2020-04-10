package com.example.ecommerce.Model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Cart (var pname : String? = "", var discount : String? = "", var price : String? = "", var pid : String? = "", var quantity : String? = "")