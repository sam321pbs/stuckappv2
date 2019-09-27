package com.sammengistu.stuckfirebase.data

data class DeviceTokenModel(
    val ownerId: String,
    val ownerRef: String,
    val token: String
) : FirebaseItem(ownerId, ownerRef) {
    constructor() : this("", "", "")
}