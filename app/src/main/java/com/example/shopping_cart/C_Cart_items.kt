package com.example.shopping_cart

class C_Cart_items {
    var cart_name: String? = null
    var cart_Price: String? = null
    var cart_ID: String? = null
    var cart_bgColor: String? = null

    constructor() {}
    constructor(cart_name: String?, cart_Price: String?, cart_ID: String?, cart_bgColor: String?) {
        this.cart_name = cart_name
        this.cart_Price = cart_Price
        this.cart_ID = cart_ID
        this.cart_bgColor = cart_bgColor
    }
}