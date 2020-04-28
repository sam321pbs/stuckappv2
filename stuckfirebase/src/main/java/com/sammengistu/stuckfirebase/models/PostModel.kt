package com.sammengistu.stuckfirebase.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.sammengistu.stuckfirebase.constants.*

@Entity(tableName = "posts")
open class PostModel(
    val ownerRef: String,
    val question: String,
    val privacy: String,
    val category: String,
    val type: String,
    var image1: String,
    var image2: String,
    var choice1: String,
    var choice2: String,
    var choice3: String,
    var choice4: String,
    var votes1: Int = 0,
    var votes2: Int = 0,
    var votes3: Int = 0,
    var votes4: Int = 0,
    var totalStars: Int = 0,
    var totalComments: Int = 0
) : FirebaseItem(ownerRef) {

    // _id is the id inside the local db
    @Exclude
    @PrimaryKey(autoGenerate = true)
    var _id: Int? = null

    @Ignore
    @Exclude
    var owner: UserModel? = null

    constructor() :
            this("", "", "", "", "",
                "", "", "", "", "", "")

    constructor(ownerRef: String,
                question: String,
                privacy: String,
                category: String,
                type: String) : this(ownerRef, question, privacy, category, type,
        "", "", "", "", "", "")

    /**
     * creates a map to send updates to server
     */
    @Exclude
    fun convertPostUpdatesToMap(): Map<String, Any> {
        return mapOf(
            Pair("totalStars", totalStars),
            Pair("totalComments", totalComments),
            Pair("votes1", votes1),
            Pair("votes2", votes2),
            Pair("votes3", votes3),
            Pair("votes4", votes4)
        )
    }

    /**
     * imageLocation - can be a location on the device or a url in firestorage
     */
    @Exclude
    fun addImage(imageLocation: String) {
        if (image1.isBlank()) {
            image1 = imageLocation
        } else {
            image2 = imageLocation
        }
    }

    @Exclude
    fun addChoice(choice: String) {
        when {
            choice1.isBlank() -> {
                choice1 = choice
            }
            choice2.isBlank() -> {
                choice2 = choice
            }
            choice3.isBlank() -> {
                choice3 = choice
            }
            else -> {
                choice4 = choice
            }
        }
    }

    /**
     * return a list of all the choices
     */
    @Exclude
    fun choicesAsList() : List<ChoiceModel> {
        if (type == PostType.TEXT.toString()) {
            val choiceList = mutableListOf(
                ChoiceModel(ID_CHOICE_1, choice1, votes1),
                ChoiceModel(ID_CHOICE_2, choice2, votes2)
            )
            if (choice3.isNotBlank()) {
                choiceList.add(ChoiceModel(ID_CHOICE_3, choice3, votes3))
            }
            if (choice4.isNotBlank()) {
                choiceList.add(ChoiceModel(ID_CHOICE_4, choice4, votes4))
            }
            return choiceList
        } else {
            return mutableListOf(
                ChoiceModel(ID_IMAGE_1, image1, votes1),
                ChoiceModel(ID_IMAGE_2, image2, votes2)
            )
        }
    }

    @Exclude
    fun getTotalVotes(): Int = votes1 + votes2 +  votes3 + votes4
}