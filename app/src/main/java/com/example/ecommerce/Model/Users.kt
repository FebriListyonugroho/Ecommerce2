package com.example.ecommerce.Model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Users (var name : String? = "", var phone : String? ="", var password : String? ="", var address : String? = "", var image :String? = "")
