package com.example.shopping_cart

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class M_ProductItem_Adapter(
    private val context: Context,
    private val c_product_items: ArrayList<C_Product_items>,
    private val notifPurchaseDetails: TextView,
    private val showNotifBtn: CardView,
    private val notifBackgroundColor: ConstraintLayout
) : RecyclerView.Adapter<M_ProductItem_Adapter.ViewHolder>() {
    private val firebaselink =
        "https://shopping-cart-6e16b-default-rtdb.asia-southeast1.firebasedatabase.app/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = c_product_items[position]
        val productBackground = c_product_items[position].bgColor
        val adapterPosition = holder.adapterPosition
        val resourceId = when (product.id) {
            "p_1" -> R.drawable.p_1
            "p_2" -> R.drawable.p_2
            "p_3" -> R.drawable.p_3
            "p_4" -> R.drawable.p_4
            "p_5" -> R.drawable.p_5
            else -> R.drawable.circle_white_black // Default drawable resource ID
        }
        val color = Color.parseColor(productBackground)
        val priceString = c_product_items[position].price
        val priceDouble = priceString!!.toDouble()
        val priceInt = priceDouble.toInt()
        holder.productImage.setImageResource(resourceId)
        holder.productImage.setBackgroundColor(color)
        holder.productCategory.text = c_product_items[position].category
        holder.productName.text = c_product_items[position].name
        holder.productPrice.text = "$$priceInt"
        holder.addItemBtn.setOnClickListener {
            // Show Notif after adding
            val builder = SpannableStringBuilder()
            val boldProductName = SpannableString(c_product_items[adapterPosition].name)
            boldProductName.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                boldProductName.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(boldProductName).append(" has been added to your cart.")

            // Append the rest of the text
            showNotifBtn.visibility = View.VISIBLE
            notifPurchaseDetails.text = builder
            notifBackgroundColor.setBackgroundColor(color)
            FirebaseDatabase.getInstance(firebaselink).reference.child("purchase")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Update total purchase
                        if (snapshot.exists()) {
                            val price = c_product_items[adapterPosition].price ?: "Default Price"
                            val name = c_product_items[adapterPosition].name ?: "Default Name"
                            val bgColor = c_product_items[adapterPosition].bgColor ?: "#FFFFFF"
                            // Save data in the database
                            val hashMap: HashMap<String, Any> = HashMap()
                            hashMap["item price"] = price
                            hashMap["item name"] = name
                            hashMap["item bgGround"] = bgColor
                            FirebaseDatabase.getInstance(firebaselink).reference.child("purchase")
                                .child("purchasedetails").push().setValue(hashMap)
                        } else {
                            // Save data in the database for the first time
                            val price = c_product_items[adapterPosition].price ?: "Default Price"
                            val name = c_product_items[adapterPosition].name ?: "Default Name"
                            val bgColor = c_product_items[adapterPosition].bgColor ?: "#FFFFFF"

                            val hashMap: HashMap<String, Any> = HashMap()
                            hashMap["item price"] = price
                            hashMap["item name"] = name
                            hashMap["item bgGround"] = bgColor
                            FirebaseDatabase.getInstance(firebaselink).reference.child("purchase")
                                .child("purchasedetails").push().setValue(hashMap)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle onCancelled if needed
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return c_product_items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productCategory: TextView = itemView.findViewById(R.id.P_Product_category)
        val productName: TextView = itemView.findViewById(R.id.P_Product_name)
        val productPrice: TextView = itemView.findViewById(R.id.P_Product_Price)
        val productImage: ImageView = itemView.findViewById(R.id.P_Product_image)
        val addItemBtn: ConstraintLayout = itemView.findViewById(R.id.Add_item_Btn)
    }
}