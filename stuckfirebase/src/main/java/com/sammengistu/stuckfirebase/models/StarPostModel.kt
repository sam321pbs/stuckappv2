package com.sammengistu.stuckfirebase.models

class StarPostModel(
    ownerId: String,
    question: String,
    privacy: String,
    category: String,
    type: String,
    image1: String,
    image2: String,
    choice1: String,
    choice2: String,
    choice3: String,
    choice4: String,
    votes1: Int = 0,
    votes2: Int = 0,
    votes3: Int = 0,
    votes4: Int = 0,
    totalStars: Int = 0,
    totalComments: Int = 0,
    val postRef: String,
    val starPostOwnerRef: String
) : PostModel(
    ownerId,
    question,
    privacy,
    category,
    type,
    image1,
    image2,
    choice1,
    choice2,
    choice3,
    choice4,
    votes1,
    votes2,
    votes3,
    votes4,
    totalStars,
    totalComments
) {
    constructor(starOwnerRef: String, post: PostModel) :
            this(
                post.ownerRef,
                post.question,
                post.privacy,
                post.category,
                post.type,
                post.image1,
                post.image2,
                post.choice1,
                post.choice2,
                post.choice3,
                post.choice4,
                post.votes1,
                post.votes2,
                post.votes3,
                post.votes4,
                post.totalStars,
                post.totalComments,
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
                "",
                "",
                "",
                "",
                0, 0,0,0,0,0,
                "",
                ""
            )

}