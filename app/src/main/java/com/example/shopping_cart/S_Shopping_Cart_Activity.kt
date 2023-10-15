package com.example.shopping_cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class S_Shopping_Cart_Activity : AppCompatActivity() {
    private var Cart_TotalPurchase: TextView? = null
    private var Cart_Total_price: TextView? = null
    private var GoBack_ShoppingBTN: TextView? = null
    private var Buy_Now_Btn: TextView? = null
    private var CartRef: DatabaseReference? = null
    private var totalPurchaseReference: DatabaseReference? = null
    private var totalPurchaseListener: ValueEventListener? = null
    private val firebaselink =
        "https://shopping-cart-6e16b-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private var CartRecyclerview: RecyclerView? = null
    private var c_cart_items_list: ArrayList<C_Cart_items>? = null
    private var m_cartItem_adapter: M_CartItem_Adapter? = null
    private var CartisEmptyTxt: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)
        Cart_TotalPurchase = findViewById(R.id.Cart_TotalPurchase)
        Cart_Total_price = findViewById(R.id.Cart_Total_price)
        GoBack_ShoppingBTN = findViewById(R.id.GoBack_ShoppingBTN)
        Buy_Now_Btn = findViewById(R.id.Buy_Now_Btn)
        CartisEmptyTxt = findViewById(R.id.CartisEmptyTxt)
        CartRecyclerview = findViewById(R.id.cart_recyclerview)




        val CartLayoutManager = LinearLayoutManager(this)
        CartRecyclerview!!.layoutManager = CartLayoutManager
        CartRecyclerview!!.setHasFixedSize(true)
        CartRef = FirebaseDatabase.getInstance(firebaselink).reference.child("purchase")
            .child("purchasedetails")
        c_cart_items_list = ArrayList()

        GoBack_ShoppingBTN!!.setOnClickListener {
            val intent = Intent(this@S_Shopping_Cart_Activity, S_Product_Activity::class.java)
            startActivity(intent)
            finish()
        }

        showCurrentItemsCart()
        displayTotalPriceAndPurchase()

        Buy_Now_Btn!!.setOnClickListener {
            if (c_cart_items_list!!.isEmpty()) {
                Toast.makeText(
                    this@S_Shopping_Cart_Activity,
                    "There is no item",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent =
                    Intent(this@S_Shopping_Cart_Activity, S_Confirmation_Activity::class.java)
                intent.putExtra("TotalPrice", Cart_Total_price!!.text.toString())
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showCurrentItemsCart() {
        val queryCart: Query? = CartRef
        queryCart!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot1 in snapshot.children) {
                        val uniqueID = snapshot1.key
                        val c_cart_items = C_Cart_items().apply {
                            cart_ID = uniqueID
                            cart_bgColor = snapshot1.child("item bgGround").value.toString()
                            cart_name = snapshot1.child("item name").value.toString()
                            cart_Price = snapshot1.child("item price").value.toString()
                        }
                        c_cart_items_list!!.add(c_cart_items)
                    }
                    m_cartItem_adapter = M_CartItem_Adapter(
                        applicationContext,
                        c_cart_items_list!!,
                        Cart_Total_price!!,
                        CartisEmptyTxt!!
                    )
                    CartRecyclerview!!.adapter = m_cartItem_adapter
                    m_cartItem_adapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun displayTotalPriceAndPurchase() {
        totalPurchaseReference =
            FirebaseDatabase.getInstance(firebaselink).reference.child("purchase")
        totalPurchaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val total = snapshot.child("purchasedetails").childrenCount.toString()
                    Cart_TotalPurchase!!.visibility = View.VISIBLE
                    Cart_TotalPurchase!!.text = total
                } else {
                    Cart_TotalPurchase!!.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        }
        totalPurchaseReference!!.addValueEventListener(totalPurchaseListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        totalPurchaseReference?.removeEventListener(totalPurchaseListener!!)
    }
}
