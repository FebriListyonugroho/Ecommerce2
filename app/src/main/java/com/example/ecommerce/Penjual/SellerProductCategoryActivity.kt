package com.example.ecommerce.Penjual

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.ecommerce.R

class SellerProductCategoryActivity : AppCompatActivity() {

    private var tShirts : ImageView? = null
    private var sportTShirt : ImageView? = null
    private var femaleDresses : ImageView? = null
    private var sweathers : ImageView? = null

    private var glasses : ImageView? = null
    private var walletsBagsPurses : ImageView? = null
    private var hatscaps : ImageView? = null
    private var shoes : ImageView? = null
    private var headPhoneHandFree : ImageView? = null

    private var Laptops : ImageView? = null
    private var watches : ImageView? = null
    private var mobilePhones : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_product_category)

        tShirts = findViewById(R.id.t_shirts)
        sportTShirt = findViewById(R.id.sports_t_shirts)
        femaleDresses = findViewById(R.id.female_dresses)
        sweathers = findViewById(R.id.sweather)

        glasses = findViewById(R.id.glasses)
        walletsBagsPurses = findViewById(R.id.purses_bags_wallets)
        hatscaps = findViewById(R.id.hats_caps)
        shoes = findViewById(R.id.shoes)

        headPhoneHandFree = findViewById(R.id.headphones_handfree)
        Laptops = findViewById(R.id.laptop_pc)
        watches = findViewById(R.id.watches)
        mobilePhones = findViewById(R.id.mobilephones)


        tShirts!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "tShirts")
            startActivity(intent)
        }

        sportTShirt!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "sportTShirt")
            startActivity(intent)
        }

        femaleDresses!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "femaleDresses")
            startActivity(intent)
        }

        sweathers!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "sweathers")
            startActivity(intent)
        }

        glasses!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "glasses")
            startActivity(intent)
        }

        walletsBagsPurses!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "walletsBagsPurses")
            startActivity(intent)
        }

        hatscaps!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "hatsCaps")
            startActivity(intent)
        }

        shoes!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "shoes")
            startActivity(intent)
        }

        headPhoneHandFree!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "headPhoneHandFree")
            startActivity(intent)
        }

        Laptops!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "Laptops")
            startActivity(intent)
        }

        watches!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "watches")
            startActivity(intent)
        }

        mobilePhones!!.setOnClickListener {v ->
            val intent = Intent(this, SellerAddNewProductActivity::class.java)
            intent.putExtra("Category", "mobilePhones")
            startActivity(intent)
        }

    }
}
