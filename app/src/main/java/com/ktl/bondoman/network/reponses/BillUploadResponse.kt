package com.ktl.bondoman.network.reponses

import com.google.gson.annotations.SerializedName

data class Item (
    @SerializedName("name")
    val name: String,

    @SerializedName("qty")
    val qty: Int,

    @SerializedName("price")
    val price: Double
)

data class ItemsContainer(
    @SerializedName("items")
    val arrItems: List<Item>
)

data class BillUploadResponse(
    @SerializedName("items")
    val items: ItemsContainer
)

