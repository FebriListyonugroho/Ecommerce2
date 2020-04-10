package com.example.ecommerce.Penjual

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.ecommerce.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class SellerAddNewProductActivity : AppCompatActivity() {

    private var CategoryName : String? = null
    private var AddNewProductButton : Button? = null
    private var InputProductImage : ImageView? = null
    private var InputProductName : EditText? = null
    private var InputProdutDescription : EditText? = null
    private var InputProdutAddress : EditText? = null
    private var InputProductPrice : EditText? = null
    private val GalleryPick : Int = 1
    private var ImageUri : Uri? = null
    private var Description : String? = null
    private var Price : String? = null
    private var Pname : String? = null
    private var Address : String? = null
    private var saveCurrentDate : String? = null
    private var saveCurrentTime : String? = null
    private var productRandomKey : String? = null
    private var ProductImageRef : StorageReference? = null
    private var ProductsRef : DatabaseReference? = null
    private var sellersRef : DatabaseReference? = null
    private var downloadImageUrl : String? = null
    private var loadingBar : ProgressDialog? = null
    private var sName : String? = null
    private var sAddress : String? = null
    private var sPhone : String? = null
    private var sEmail : String? = null
    private var sID : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_add_new_product)

        CategoryName = intent.extras.get("Category").toString()
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Images")
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products")
        sellersRef = FirebaseDatabase.getInstance().getReference("Sellers")

        AddNewProductButton = findViewById(R.id.add_new_product)
        InputProductImage = findViewById(R.id.select_product_image)
        InputProductName = findViewById(R.id.product_name)
        InputProdutDescription = findViewById(R.id.product_description)
        InputProductPrice = findViewById(R.id.product_price)
        InputProdutAddress = findViewById(R.id.product_address)

        loadingBar = ProgressDialog(this)

        InputProductImage!!.setOnClickListener { v ->
            OpenGallery()
        }

        AddNewProductButton!!.setOnClickListener { v ->
            ValidateProductData()
        }

        sellersRef!!.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.exists()){
                        sName = p0.child("name").getValue().toString()
                        sPhone = p0.child("phone").getValue().toString()
                        sAddress = p0.child("address").getValue().toString()
                        sEmail = p0.child("email").getValue().toString()
                        sID = p0.child("sid").getValue().toString()
                    }

                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

    }

    private fun OpenGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT)
        galleryIntent.setType("image/*")
        startActivityForResult(galleryIntent, GalleryPick)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==GalleryPick && resultCode== Activity.RESULT_OK && data != null){
            ImageUri = data.data
            InputProductImage!!.setImageURI(ImageUri)
        }
    }

    private fun ValidateProductData() {
        Description = InputProdutDescription!!.text.toString()
        Price = InputProductPrice!!.text.toString()
        Pname = InputProductName!!.text.toString()
        Address = InputProdutAddress!!.text.toString()


        if (ImageUri == null){
            Toast.makeText(this, "Harap Pasang Gambar Untuk Iklan Product ...", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Harap Isi Deskripsi Product ...", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Harap Isi Harga ...", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(Pname)){
            Toast.makeText(this, "Harap Isi Nama Produk ...", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(Address)){
            Toast.makeText(this, "Harap Isi Alamat ...", Toast.LENGTH_SHORT).show()
        }else{
            StoreProductInformantion()
        }
    }

    private fun StoreProductInformantion() {

        loadingBar!!.setTitle("Menambahkan Produk Baru")
        loadingBar!!.setMessage("Harap Tunggu")
        loadingBar!!.setCanceledOnTouchOutside(false)
        loadingBar!!.show()

        val calender = Calendar.getInstance()

        val currentDate = SimpleDateFormat("MMM dd, yyyy")
        saveCurrentDate = currentDate.format(calender.time)

        val currentTime = SimpleDateFormat("HH:mm:ss a")
        saveCurrentTime = currentTime.format(calender.time)

        productRandomKey = saveCurrentDate + saveCurrentTime

        val filePath = ProductImageRef!!.child(ImageUri!!.lastPathSegment + productRandomKey + ".jpg")

        val uploadTask = filePath.putFile(ImageUri!!)

        uploadTask.addOnFailureListener(object : OnFailureListener{
            override fun onFailure(p0: Exception) {
                val message = p0.toString()
                Toast.makeText(this@SellerAddNewProductActivity, "Error : ${message}", Toast.LENGTH_SHORT).show()
                loadingBar!!.dismiss()
            }
        }).addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot>{
            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                Toast.makeText(this@SellerAddNewProductActivity, "Product Image uploaded Successfully ...", Toast.LENGTH_SHORT).show()

                val uriTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                        loadingBar!!.dismiss()
                    }
                    downloadImageUrl = filePath.downloadUrl.toString()
                    return@Continuation filePath.downloadUrl
                }).addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        downloadImageUrl = task.getResult().toString()

                        Toast.makeText(this@SellerAddNewProductActivity, "got the Product image Url Successfully", Toast.LENGTH_SHORT).show()
                        SaveProductInfoToDatabase()
                    }
                }
            }
        })

    }

    private fun SaveProductInfoToDatabase(){
        val productMap:HashMap<String, Any> = HashMap()
        productMap.put("pid", productRandomKey!!)
        productMap.put("date", saveCurrentDate!!)
        productMap.put("time", saveCurrentTime!!)
        productMap.put("address", Address!!)
        productMap.put("description", Description!!)
        productMap.put("image", downloadImageUrl!!)
        productMap.put("category", CategoryName!!)
        productMap.put("price", Price!!)
        productMap.put("pname", Pname!!)

        productMap.put("sellerName", sName!!)
        productMap.put("sellerAddress", sAddress!!)
        productMap.put("sellerPhone", sPhone!!)
        productMap.put("sellerEmail", sEmail!!)
        productMap.put("sid", sID!!)
        productMap.put("productState", "Not Approved")

        ProductsRef!!.child(productRandomKey!!).updateChildren(productMap).addOnCompleteListener { task ->
            if (task.isSuccessful){

                startActivity(Intent(this, SellerHomeActivity::class.java))

                loadingBar!!.dismiss()
                Toast.makeText(this, "Product is added successfully ...",Toast.LENGTH_SHORT).show()
            }else{
                loadingBar!!.dismiss()
                val message = task.exception.toString()
                Toast.makeText(this, "Error : ${message}", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
