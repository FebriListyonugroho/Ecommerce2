package com.example.ecommerce.Admin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.ecommerce.Pembeli.HomeActivity
import com.example.ecommerce.Pembeli.MainActivity
import com.example.ecommerce.R

class AdminHomeActivity : AppCompatActivity() {

    private var AdminListOrderBtn : Button? = null
    private var AdminlogoutBtn : Button? = null
    private var maintainProductsBtn : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        AdminListOrderBtn = findViewById(R.id.check_approve_orders_btn)
        AdminlogoutBtn = findViewById(R.id.admin_logout_btn)
        maintainProductsBtn = findViewById(R.id.maintain_btn)

        maintainProductsBtn!!.setOnClickListener { v ->
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("Admin", "Admin")
            startActivity(intent)
        }

        AdminlogoutBtn!!.setOnClickListener { v ->
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        AdminListOrderBtn!!.setOnClickListener{v ->
            startActivity(Intent(this, AdminNewOrdersActivity::class.java))
        }

    }
}
