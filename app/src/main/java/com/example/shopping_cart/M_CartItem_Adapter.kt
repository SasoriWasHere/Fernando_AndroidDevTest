package com.example.shopping_cart

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class M_CartItem_Adapter(
    private val context: Context,
    private var c_cart_items: ArrayList<C_Cart_items>,
    private val Cart_Total_price: TextView,
    private val CartisEmptyTxt: TextView
) : RecyclerView.Adapter<M_CartItem_Adapter.ViewHolder>() {
    private val firebaselink =
        "https://shopping-cart-6e16b-default-rtdb.asia-southeast1.firebasedatabase.app/"

    init {
        calculateTotalPrice()
    }

    private fun calculateTotalPrice() {
        var totalPricex = 0
        for (cartItem in c_cart_items) {
            val priceString = cartItem.cart_Price
            val price = priceString!!.toDouble()
            totalPricex += price.toInt()
        }
        Cart_Total_price.text = "$$totalPricex"
        Cart_Total_price.visibility = View.VISIBLE
        if (c_cart_items.isEmpty()) {
            CartisEmptyTxt.visibility = View.VISIBLE
        } else {
            CartisEmptyTxt.visibility = View.GONE
        }
    }

    fun updateData(newData: ArrayList<C_Cart_items>) {
        c_cart_items = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_purchase, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //getting the total price for all
        val adapterPosition = holder.adapterPosition
        val ProductID = c_cart_items[position].cart_ID
        val ProductBackground = c_cart_items[position].cart_bgColor
        val color = Color.parseColor(ProductBackground)
        val priceString = c_cart_items[position].cart_Price
        val priceDouble = priceString!!.toDouble()
        val priceInt = priceDouble.toInt()
        holder.cart_itemBg.setBackgroundColor(color)
        holder.CartPrice.text = "$$priceInt"
        holder.Cartname.text = c_cart_items[position].cart_name
        holder.purchase_cancelbtn.setOnClickListener {
            FirebaseDatabase.getInstance(firebaselink).reference.child("purchase")
                .child("purchasedetails").child(
                ProductID!!
            ).removeValue().addOnCompleteListener {
                c_cart_items.removeAt(adapterPosition)
                updateData(c_cart_items)
                calculateTotalPrice()
            }
        }
    }

    override fun getItemCount(): Int {
        return c_cart_items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var Cartname: TextView
        var CartPrice: TextView
        var cart_itemBg: ConstraintLayout
        var purchase_cancelbtn: ImageView

        init {
            cart_itemBg = itemView.findViewById(R.id.cart_itemBg)
            Cartname = itemView.findViewById(R.id.purchase_ItemName)
            CartPrice = itemView.findViewById(R.id.purchase_item_price)
            purchase_cancelbtn = itemView.findViewById(R.id.purchase_cancelbtn)
        }
    }
}