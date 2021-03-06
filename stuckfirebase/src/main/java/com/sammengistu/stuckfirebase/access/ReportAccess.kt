package com.sammengistu.stuckfirebase.access

import com.sammengistu.stuckfirebase.constants.REPORTS
import com.sammengistu.stuckfirebase.models.ReportModel

class ReportAccess : FirebaseItemAccess<ReportModel>() {
    override fun getCollectionRef() = getEnvironmentCollectionRef(REPORTS)
    override fun getModelClass() = ReportModel::class.java
}