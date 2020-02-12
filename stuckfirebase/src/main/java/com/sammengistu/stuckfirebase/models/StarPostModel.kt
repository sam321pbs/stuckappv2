package com.sammengistu.stuckfirebase.models

class StarPostModel(
    ownerId: String,
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
    val starPostOwnerRef: String
) : PostModel(
    ownerId,
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
    constructor(starOwnerRef: String, post: PostModel) :
            this(
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
                starOwnerRef
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
                0,
                0,
                HashMap<String, String>(),
                HashMap<String, String>(),
                HashMap<String, Int>(),
                "",
                ""
            )

}