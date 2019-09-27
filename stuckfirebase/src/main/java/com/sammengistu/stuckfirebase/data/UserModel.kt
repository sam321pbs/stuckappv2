package com.sammengistu.stuckfirebase.data

data class UserModel(
    var userId: String,
    var username: String,
    var avatar: String,
    var name: String,
    var occupation: String,
    var education: String,
    var bio: String,
    var ageGroup: Int,
    var gender: Int,
    var totalMadeVotes: Int,
    var totalReceivedVotes: Int,
    var totalReceivedStars: Int
) : FirebaseItem(userId, "") {
    constructor() : this(  "", "", "", "", "", "", "", -1, -1, 0,0,0)

    fun convertUserToMap(): Map<String, Any> {
        return mapOf(
            Pair("username", username),
            Pair("avatar", avatar),
            Pair("name", name),
            Pair("occupation", occupation),
            Pair("education", education),
            Pair("bio", bio),
            Pair("ageGroup", ageGroup),
            Pair("gender", gender)
        )
    }

    fun isEqualTo(updateUser: UserModel): Boolean {
        return username == updateUser.username &&
                avatar == updateUser.avatar &&
                name == updateUser.name &&
                education == updateUser.education &&
                occupation == updateUser.occupation &&
                bio == updateUser.bio &&
                ageGroup == updateUser.ageGroup &&
                gender == updateUser.gender

    }
}