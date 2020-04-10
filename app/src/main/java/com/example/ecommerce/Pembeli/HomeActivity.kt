package com.example.ecommerce.Pembeli

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.widget.TextView
import com.example.ecommerce.Admin.AdminMaintainProductsActivity
import com.example.ecommerce.Model.Products
import com.example.ecommerce.Prevalent.Prevalent
import com.example.ecommerce.R
import com.example.ecommerce.ViewHolder.ProductViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.paperdb.Paper

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var options: FirebaseRecyclerOptions<Products>? = null
    private var ProductRef: DatabaseReference? = null
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var gridLayoutManager: GridLayoutManager

    var type : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val intent = intent
        val bundle = intent.extras
        if (bundle != null){
            type = intent.extras.get("Admin").toString()
        }

        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products")

        Paper.init(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("Home")

        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->

            if (!type.equals("Admin")){
                startActivity(Intent(this, CartActivity::class.java))
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val headerView = navView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.user_profile_name)
        val profileImageView = headerView.findViewById<CircleImageView>(R.id.user_profile_image)

        ///Digunakan untuk melakukan cek nama pengguna, disini jika tidak admin maka akan muncul foto profil dan nama pengguna
        if (!type.equals("Admin")){

            userNameTextView.setText(Prevalent.CurrentOnlineUser!!.name!!)
            Picasso.get().load(Prevalent.CurrentOnlineUser!!.image).placeholder(R.drawable.profile).into(profileImageView)

        }

        recyclerView = findViewById(R.id.recycler_menu)
        recyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView!!.layoutManager = gridLayoutManager
    }

    override fun onStart() {
        super.onStart()


        ///.orderByChild("productState").equalTo("Approved")
        options = FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(ProductRef!!, Products::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Products, ProductViewHolder>(options!!){
            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Products) {

                holder.txtproductName!!.setText(model.pname)
                holder.txtaddress!!.setText(model.address)
                holder.txtproductPrice!!.setText("Rp. ${model.price}")
                Picasso.get().load(model.image).into(holder.imageView)

                holder.itemView.setOnClickListener { v ->
                    //Jika ingin mengedit product oleh admin jika else akan dialihkan ke user
                    if (type.equals("Admin")){

                        val intent = Intent(this@HomeActivity, AdminMaintainProductsActivity::class.java)
                        intent.putExtra("pid", model.pid)
                        startActivity(intent)

                    }else{

                        val intent = Intent(this@HomeActivity, ProductDetailsActivity::class.java)
                        intent.putExtra("pid", model.pid)
                        startActivity(intent)
                    }
                }

            }

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProductViewHolder {
                val view = LayoutInflater.from(p0.context).inflate(R.layout.product_items_layout, p0, false)
                val holder = ProductViewHolder(view)
                return holder
            }
        }
        recyclerView!!.adapter = adapter
        adapter.startListening()
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
//            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_cart -> {
                if (!type.equals("Admin")){
                    startActivity(Intent(this, CartActivity::class.java))
                }
            }
            R.id.nav_search -> {
                if (!type.equals("Admin")) {
                    startActivity(Intent(this, SearchProductActivity::class.java))
                }
            }
            R.id.nav_categories -> {

            }
            R.id.nav_settings -> {
                if (!type.equals("Admin")) {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
            R.id.nav_logout -> {
                if (!type.equals("Admin")) {
                    Paper.book().destroy()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
