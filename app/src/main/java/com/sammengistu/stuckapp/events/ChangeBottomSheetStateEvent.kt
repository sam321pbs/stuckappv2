package com.sammengistu.stuckapp.events

import com.sammengistu.stuckfirebase.models.PostModel

class ChangeBottomSheetStateEvent(val show: Boolean, val post: PostModel? = null)