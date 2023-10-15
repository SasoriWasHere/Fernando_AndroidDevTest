package com.example.shopping_cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class S_Product_Activity : AppCompatActivity() {
    private val firebaselink =
        "https://shopping-cart-6e16b-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private var productRef: DatabaseReference? = null
    private var totalPurchaseReference: DatabaseReference? = null
    private var totalPurchaseListener: ValueEventListener? = null
    private var shoppingCartBtn: ImageView? = null
    private var showNotifBtn: CardView? = null
    private var closeNotifBtn: ImageView? = null
    private var notifPurchaseDetails: TextView? = null
    private var notifBackgroundColor: ConstraintLayout? = null
    private var totalPurchaseTextView: TextView? = null
    private var productRecyclerView: RecyclerView? = null
    private var productItemsList: ArrayList<C_Product_items>? = null
    private var productItemAdapter: M_ProductItem_Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        FirebaseApp.initializeApp(this)

        totalPurchaseTextView = findViewById(R.id.Total_Purchase1)
        shoppingCartBtn = findViewById(R.id.ShopCartBtn)
        notifPurchaseDetails = findViewById(R.id.Notif_PurchaseDetails)
        notifBackgroundColor = findViewById(R.id.Notif_BackgroundColor)
        showNotifBtn = findViewById(R.id.ShowNotif_Btn)
        closeNotifBtn = findViewById(R.id.Close_Notif_btn)
        closeNotifBtn?.setOnClickListener { view -> showNotifBtn?.visibility = View.GONE }

        productRecyclerView = findViewById(R.id.Show_Shop_Recyclerview)
        val productLayoutManager = LinearLayoutManager(this)
        productRecyclerView?.layoutManager = productLayoutManager
        productRecyclerView?.setHasFixedSize(true)

        productRef = FirebaseDatabase.getInstance(firebaselink).reference.child("products")
        productItemsList = ArrayList()
        fetchProductData()
        observeTotalItemPurchase()

        shoppingCartBtn?.setOnClickListener {
            val intent = Intent(this@S_Product_Activity, S_Shopping_Cart_Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun observeTotalItemPurchase() {
        totalPurchaseReference = FirebaseDatabase.getInstance(firebaselink).reference.child("purchase")
        totalPurchaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val total = snapshot.child("purchasedetails").childrenCount.toString()
                    totalPurchaseTextView?.visibility = View.VISIBLE
                    totalPurchaseTextView?.text = total
                } else {
                    totalPurchaseTextView?.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        }
        totalPurchaseReference?.addValueEventListener(totalPurchaseListener!!)
    }

    private fun fetchProductData() {
        val queryProduct: Query? = productRef
        queryProduct?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot1 in snapshot.children) {
                        val productItem = C_Product_items().apply {
                            id = snapshot1.child("id").value.toString()
                            bgColor = snapshot1.child("bgColor").value.toString()
                            category = snapshot1.child("category").value.toString()
                            price = snapshot1.child("price").value.toString()
                            name = snapshot1.child("name").value.toString()
                        }
                        productItemsList?.add(productItem)
                    }
                    productItemAdapter = M_ProductItem_Adapter(
                        applicationContext,
                        productItemsList!!,
                        notifPurchaseDetails!!,
                        showNotifBtn!!,
                        notifBackgroundColor!!
                    )
                    productRecyclerView?.adapter = productItemAdapter
                    productItemAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        totalPurchaseReference?.removeEventListener(totalPurchaseListener!!)
    }
}