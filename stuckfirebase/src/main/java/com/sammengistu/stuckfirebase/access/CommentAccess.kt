package com.sammengistu.stuckfirebase.access

import com.google.firebase.firestore.CollectionReference
import com.sammengistu.stuckfirebase.constants.POST_COMMENTS
import com.sammengistu.stuckfirebase.data.CommentModel
import com.sammengistu.stuckfirebase.events.IncreaseCommentCountEvent
import org.greenrobot.eventbus.EventBus

class CommentAccess : FirebaseItemAccess<CommentModel>() {
    override fun getCollectionRef(): CollectionReference {
        return getEnvironmentCollectionRef(POST_COMMENTS)

        /* having issues with cloud functions because I am adding an empty document and triggers won't fire
        * this is the error reported in cloud functions logs
            Error: Value for argument "documentPath" must point to a document, but was "ZMSsCufZa3BuTsV56IyV". Your path does not contain an even number of components.
                 at Firestore.doc (/srv/node_modules/@google-cloud/firestore/build/src/index.js:416:19)
                 at exports.updateCommentCountTrigger.functions.firestore.document.onWrite (/srv/index.js:49:25)
                 at cloudFunction (/srv/node_modules/firebase-functions/lib/cloud-functions.js:131:23)
                 at /worker/worker.js:825:24
                 at <anonymous>
                 at process._tickDomainCallback (internal/process/next_tick.js:229:7)
         */
//            .document(postId)
//            .collection(COMMENTS)
    }

    override fun getModelClass(): Class<CommentModel> {
        return CommentModel::class.java
    }

    override fun onItemCreated(item: CommentModel) {
        EventBus.getDefault().post(IncreaseCommentCountEvent(item.postRef))
    }
}