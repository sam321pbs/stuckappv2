package com.sammengistu.stuckfirebase.data

import com.google.firebase.firestore.Exclude

@Exclude
const val MAX_NUMBER_OF_CHOICES = 4

data class PostModel(
    val ownerId: String,
    val ownerRef: String,
    val userName: String,
    val avatar: String,
    val question: String,
    val privacy: String,
    val category: String,
    val type: String,
    var totalStars: Int = 0,
    var totalComments: Int = 0,
    var images: HashMap<String, String> = HashMap(),
    var choices: HashMap<String, String> = HashMap(),
    var votes: HashMap<String, Int> = HashMap()
) : FirebaseItem() {

    init {
        if (votes.isEmpty()) {
            addEmptyVotes()
        }
    }

    constructor():
            this("", "","", "", "", "", "", "")

    constructor(
        ownerId: String,
        ownerRef: String,
        userName: String,
        avatar: String,
        question: String,
        privacy: String,
        category: String,
        type: String,
        createdAt: Any,
        totalStars: Int,
        totalComments: Int,
        images: HashMap<String, String>,
        choices: HashMap<String, String>,
        votes: HashMap<String, Int>
    ) : this(ownerId, ownerRef, userName, avatar, question, privacy, category, type) {
        this.createdAt = createdAt
        this.totalStars = totalStars
        this.totalComments = totalComments
        this.images = images
        this.choices = choices
        this.votes = votes
    }

    @Exclude
    fun addImage(loc: String) {
        val imageKey = (images.size + 1).toString()
        images.put(imageKey, loc)
    }

    @Exclude
    fun addChoice(choice: String) {
        if (choice.isNotEmpty()) {
            val choiceKey = (choices.size + 1).toString()
            choices.put(choiceKey, choice)
        }
    }

    @Exclude
    fun addEmptyVotes() {
        for (i in 1..MAX_NUMBER_OF_CHOICES) {
            val votesKey = (votes.size + 1).toString()
            votes.put(votesKey, 0)
        }
    }

    @Exclude
    fun getTotalVotes(): Int {
        var total = 0
        for (num in votes) {
            total += num.value
        }
        return total
    }

    @Exclude
    fun getChoicesToVoteList(): List<Triple<String, String, Int>> {
        val list = mutableListOf<Triple<String, String, Int>>()
        for (choice in choices) {
            list.add(Triple(choice.key, choice.value, votes[choice.key]!!))
        }
        return list
    }

    @Exclude
    fun getImagesToVoteList(): List<Triple<String, String, Int>> {
        val list = mutableListOf<Triple<String, String, Int>>()
        for (image in images) {
            list.add(Triple(image.key, image.value, votes[image.key]!!))
        }
        return list
    }
}