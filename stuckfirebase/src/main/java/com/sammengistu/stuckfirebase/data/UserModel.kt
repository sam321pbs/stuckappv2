package com.sammengistu.stuckfirebase.data

data class UserModel(
    var userId: String,
    var username: String,
    var avatar: String,
    var name: String,
    var occupation: String,
    var education: String,
    var bio: String,
    var ageGroup: String,
    var gender: String,
    var totalMadeVotes: Int,
    var totalReceivedVotes: Int,
    var totalReceivedStars: Int
) : FirebaseItem() {
    constructor() : this(  "", "", "", "", "", "", "", "", "", 0,0,0)
}