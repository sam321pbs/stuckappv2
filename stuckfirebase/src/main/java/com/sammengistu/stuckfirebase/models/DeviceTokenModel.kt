package com.sammengistu.stuckfirebase.models

data class DeviceTokenModel(
    val ownerRef: String,
    val token: String
) : FirebaseItem(ownerRef) {
    constructor() : this( "", "")
}