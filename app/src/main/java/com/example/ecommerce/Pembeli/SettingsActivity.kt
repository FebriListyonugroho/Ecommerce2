package com.example.ecommerce.Pembeli

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.ecommerce.Prevalent.Prevalent
import com.example.ecommerce.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView

class SettingsActivity : AppCompatActivity() {

    private lateinit var profileImageView : CircleImageView
    private lateinit var fullNameEditText : EditText
    private lateinit var userPhoneEditText : EditText
    private lateinit var addressEditText : EditText
    private lateinit var profileChangeTextBtn : TextView
    private lateinit var closeTextBtn : TextView
    private lateinit var saveTextButton : TextView
    private lateinit var securityQuestionBtn : Button

    private var imageUri :Uri? = null
    private var myUri : String = ""
    private lateinit var uploadTask: UploadTask
    private lateinit var storageProfilePictureRef: StorageReference
    private var checker = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures")

        profileImageView = findViewById(R.id.setting_profile_image)
        fullNameEditText = findViewById(R.id.settings_full_name)
        userPhoneEditText = findViewById(R.id.settings_phone_number)
        addressEditText = findViewById(R.id.settings_address)

        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn)
        closeTextBtn = findViewById(R.id.close_settings)
        saveTextButton = findViewById(R.id.update_account_settings_btn)
        securityQuestionBtn = findViewById(R.id.security_questions_btn)

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText)

        closeTextBtn.setOnClickListener { v ->
            finish()
        }

        saveTextButton.setOnClickListener { v ->
            if (checker.equals("clicked")){
                userInfoSaved()
            }else{
                updateOnlyUserInfo()
            }
        }

        profileChangeTextBtn.setOnClickListener { v ->
            checker = "clicked"

            CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .start(this@SettingsActivity)

        }

        securityQuestionBtn.setOnClickListener { v ->
            val intent = Intent(this, ResetPasswordActivity::class.java)
            intent.putExtra("check", "settings")
            startActivity(intent)
        }

    }

    private fun updateOnlyUserInfo() {

        val ref = FirebaseDatabase.getInstance().getReference().child("Users")

        val userMap:HashMap<String, Any> = HashMap()
        userMap.put("name", fullNameEditText.text.toString())
        userMap.put("address", addressEditText.text.toString())
        userMap.put("phoneOrder", userPhoneEditText.text.toString())
        userMap.put("image", myUri)
        ref.child(Prevalent.CurrentOnlineUser!!.phone!!).updateChildren(userMap)

        startActivity(Intent(this, HomeActivity::class.java))
        Toast.makeText(this, "Profile Update Berhasil", Toast.LENGTH_SHORT).show()
        finish()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri

            profileImageView.setImageURI(imageUri)

        }else{
            Toast.makeText(this, "Error, Try Again", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }
    }

    private fun userInfoSaved() {

        if (TextUtils.isEmpty(fullNameEditText.text.toString())){
            Toast.makeText(this, "Harap isi Nama Anda", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(addressEditText.text.toString())){
            Toast.makeText(this, "Harap isi Alamat Anda", Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(userPhoneEditText.text.toString())){
            Toast.makeText(this, "Harap isi Nomor Telpon Anda", Toast.LENGTH_SHORT).show()
        }else if (checker.equals("clicked")){

            uploadImage()
        }

    }

    private fun uploadImage() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Update Profile")
        progressDialog.setMessage("Harap tunggu, untuk melakukan update informasi profil anda")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        if (imageUri != null){
            val fileRef = storageProfilePictureRef.child(Prevalent.CurrentOnlineUser!!.phone!! + ".jpg")

            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{task ->

                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener {task ->

                if (task.isSuccessful){

                    val downloadUrl = task.getResult()
                    myUri = downloadUrl.toString()

                    val ref = FirebaseDatabase.getInstance().getReference().child("Users")

                    val userMap:HashMap<String, Any> = HashMap()
                    userMap.put("name", fullNameEditText.text.toString())
                    userMap.put("address", addressEditText.text.toString())
                    userMap.put("phoneOrder", userPhoneEditText.text.toString())
                    userMap.put("image", myUri)
                    ref.child(Prevalent.CurrentOnlineUser!!.phone!!).updateChildren(userMap)

                    progressDialog.dismiss()

                    startActivity(Intent(this, HomeActivity::class.java))
                    Toast.makeText(this, "Profile Update Berhasil", Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }

            }
        }else{
            Toast.makeText(this, "Tidak Ada Image !",Toast.LENGTH_SHORT).show()
        }

    }

    private fun userInfoDisplay(profileImageView: CircleImageView, fullNameEditText: EditText, userPhoneEditText: EditText, addressEditText: EditText) {

        val UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.CurrentOnlineUser!!.phone!!)

        UsersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    if (p0.child("image").exists()){

                        val image = p0.child("image").getValue().toString()
                        val name = p0.child("name").getValue().toString()
                        val phone = p0.child("phone").getValue().toString()
                        val address = p0.child("address").getValue().toString()

                        Picasso.get().load(image).into(profileImageView)
                        fullNameEditText.setText(name)
                        userPhoneEditText.setText(phone)
                        addressEditText.setText(address)

                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })



    }

}
