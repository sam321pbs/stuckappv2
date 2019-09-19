package com.sammengistu.stuckfirebase.data

class StarPostModel(
    ownerId: String,
    ownerRef: String,
    userName: String,
    avatar: String,
    question: String,
    privacy: String,
    category: String,
    type: String,
    totalStars: Int = 0,
    totalComments: Int = 0,
    images: HashMap<String, String> = HashMap(),
    choices: HashMap<String, String> = HashMap(),
    votes: HashMap<String, Int> = HashMap(),
    val postRef: String,
    val starPostOwnerRef: String,
    val starPostOwnerId: String
) : PostModel(
    ownerId,
    ownerRef,
    userName,
    avatar,
    question,
    privacy,
    category,
    type,
    totalStars,
    totalComments,
    images,
    choices,
    votes
) {
    constructor(starPostOwnerRef: String, starPostOwnerId: String, post: PostModel) :
            this(
                post.ownerId,
                post.ownerRef,
                post.userName,
                post.avatar,
                post.question,
                post.privacy,
                post.category,
                post.type,
                post.totalStars,
                post.totalComments,
                post.images,
                post.choices,
                post.votes,
                post.ref,
                starPostOwnerRef,
                starPostOwnerId
            )

    constructor() :
            this(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                0,
                0,
                HashMap<String, String>(),
                HashMap<String, String>(),
                HashMap<String, Int>(),
                "",
                "",
                ""
            )

}