package com.example.ecommerce.Model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Products (var pname : String? = "", var description : String? = "", var price : String? = "", var image : String? = "", var category : String? = "", var address : String? = "", var pid : String? = "", var date : String? = "", var time : String? = ""
)