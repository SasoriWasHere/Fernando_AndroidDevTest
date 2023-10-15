package com.example.shopping_cart

class C_Product_items {
    var bgColor: String? = null
    var category: String? = null
    var id: String? = null
    var name: String? = null
    var price: String? = null

    constructor() {}
    constructor(bgColor: String?, category: String?, id: String?, name: String?, price: String?) {
        this.bgColor = bgColor
        this.category = category
        this.id = id
        this.name = name
        this.price = price
    }
}