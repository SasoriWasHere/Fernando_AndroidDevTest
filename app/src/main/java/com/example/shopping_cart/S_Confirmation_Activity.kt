package com.example.shopping_cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.database.*

class S_Confirmation_Activity : AppCompatActivity() {
    private lateinit var BacktoShoppingBtn: TextView
    private lateinit var ConfirmationTotalPurchase: TextView
    private lateinit var BacktoCartBtn: ImageView
    private lateinit var switchCompat: SwitchCompat
    private lateinit var ConfirmPaymentBtn: TextView
    private lateinit var FullnameEdit: EditText
    private lateinit var EmailEdit: EditText
    private lateinit var firebaselink: String
    private var totalPurchaseReference: DatabaseReference? = null
    private var totalPurchaseListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)
        firebaselink =
            "https://shopping-cart-6e16b-default-rtdb.asia-southeast1.firebasedatabase.app/"

        val totalPrice = intent.getStringExtra("TotalPrice")

        BacktoShoppingBtn = findViewById(R.id.BacktoShopping_btn)
        BacktoCartBtn = findViewById(R.id.BacktoCart_btn)
        switchCompat = findViewById(R.id.switchCompat)
        ConfirmPaymentBtn = findViewById(R.id.Confirm_PaymentBtn)
        ConfirmationTotalPurchase = findViewById(R.id.Confirmation_TotalPurchase)
        FullnameEdit = findViewById(R.id.Fullname_editText)
        EmailEdit = findViewById(R.id.Email_editText)

        ConfirmPaymentBtn.text = "Pay $totalPrice"

        ConfirmPaymentBtn.setOnClickListener {
            val fullname = FullnameEdit.text.toString().trim()
            val email = EmailEdit.text.toString().trim()

            if (switchCompat.isChecked && fullname.isNotEmpty() && email.isNotEmpty()) {
                val intent =
                    Intent(this@S_Confirmation_Activity, S_OrderProcess_Activity::class.java)
                intent.putExtra("Buyer Name", fullname)
                intent.putExtra("Buyer Email", email)
                intent.putExtra("Buyer TotalPay", totalPrice)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this@S_Confirmation_Activity,
                    "Fill up the Fields and Agree to the terms",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        DisplayTotalItem()

        BacktoShoppingBtn.setOnClickListener {
            startActivity(Intent(this@S_Confirmation_Activity, S_Product_Activity::class.java))
            finish()
        }

        BacktoCartBtn.setOnClickListener {
            startActivity(Intent(this@S_Confirmation_Activity, S_Shopping_Cart_Activity::class.java))
            finish()
        }
    }

    private fun DisplayTotalItem() {
        totalPurchaseReference =
            FirebaseDatabase.getInstance(firebaselink).reference.child("purchase")
        totalPurchaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val total = snapshot.child("purchasedetails").childrenCount.toString()
                    ConfirmationTotalPurchase.visibility = View.VISIBLE
                    ConfirmationTotalPurchase.text = total
                } else {
                    ConfirmationTotalPurchase.visibility = View.INVISIBLE
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
