package com.example.shopping_cart

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class S_OrderProcess_Activity : AppCompatActivity() {
    private var lastSixDigits: Long = 0
    private var ReturnToProductBtn: TextView? = null
    private var cCartItemsList: ArrayList<C_Cart_items>? = null
    private var totalPay: String? = null
    private var fullName: String? = null
    private var email: String? = null
    private var orderIDText: TextView? = null
    private var firebaselink =
        "https://shopping-cart-6e16b-default-rtdb.asia-southeast1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sorder_process)
        totalPay = intent.getStringExtra("Buyer TotalPay")
        fullName = intent.getStringExtra("Buyer Name")
        email = intent.getStringExtra("Buyer Email")
        orderIDText = findViewById(R.id.Order_ID_txt)
        cCartItemsList = ArrayList()

        val currentTimeMillis = System.currentTimeMillis()
        lastSixDigits = currentTimeMillis % 1000000
        val uniqueID = "#${String.format("%06d", lastSixDigits)}"
        val orderNumber = "Your order ID is <b>$uniqueID</b>"
        orderIDText?.text = Html.fromHtml(orderNumber, Html.FROM_HTML_MODE_LEGACY)

        GetPurchaseDataAndSaveAsJSON()

        Toast.makeText(this, "Pay: $totalPay Full name: $fullName Email: $email", Toast.LENGTH_SHORT).show()

        ReturnToProductBtn = findViewById(R.id.ReturnToproduct_btn)
        ReturnToProductBtn?.setOnClickListener {
            val intent = Intent(this@S_OrderProcess_Activity, S_Product_Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun GetPurchaseDataAndSaveAsJSON() {
        val purchaseRef = FirebaseDatabase.getInstance(firebaselink).reference.child("purchase").child("purchasedetails")
        purchaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snapshot1: DataSnapshot in snapshot.children) {
                        val itemID = snapshot1.key
                        val cartItem = C_Cart_items().apply {
                            cart_ID = itemID
                            cart_name = snapshot1.child("item name").value.toString()
                            cart_Price = snapshot1.child("item price").value.toString()
                        }
                        cCartItemsList?.add(cartItem)
                    }
                    try {
                        val jsonObject = JSONObject().apply {
                            put("orderID", String.format("%06d", lastSixDigits))
                            put("totalAmount", totalPay)
                            put("buyerName", fullName)
                            put("buyerEmail", email)
                            val jsonArray = JSONArray()
                            for (cartItem: C_Cart_items in cCartItemsList!!) {
                                val itemObject = JSONObject().apply {
                                    put("itemID", cartItem.cart_ID)
                                    put("itemName", cartItem.cart_name)
                                    put("itemPrice", cartItem.cart_Price)
                                }
                                jsonArray.put(itemObject)
                            }
                            put("items", jsonArray)
                        }

                        val jsonStr = jsonObject.toString()

                        // Log the JSON data
                        Log.d("JSON", jsonStr)

                        val fileName = "order_${String.format("%06d", lastSixDigits)}.json"
                        val file = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            fileName
                        )
                        try {
                            FileOutputStream(file).use { fos ->
                                fos.write(jsonStr.toByteArray())
                                Log.d("File", "JSON data saved to file: ${file.absolutePath}")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    // Remove the purchased items from the database
                    purchaseRef.removeValue().addOnCompleteListener(
                        OnCompleteListener {
                            Toast.makeText(
                                this@S_OrderProcess_Activity,
                                "Purchase Was a Success!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled if needed
            }
        })
    }
}
