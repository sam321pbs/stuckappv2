package com.sammengistu.stuckfirebase.database

class HiddenItemsRepository(val dao: HiddenItemsDao) {

    fun getAllHiddenItems(ownerId: String) = dao.getAllItems(ownerId)

    fun getHiddenItem(id: Long) = dao.getItem(id)

    fun insertHiddenItem(item: HiddenItemModel) = dao.insertItem(item)

    fun deleteHiddenItem(item: HiddenItemModel) = dao.deleteItem(item)

    fun deleteHiddenItem(itemtId: Long) = dao.deleteByItemId(itemtId)

    companion object {
        @Volatile
        private var instance: HiddenItemsRepository? = null

        fun getInstance(hiddenItemsDao: HiddenItemsDao) =
            instance ?: synchronized(this) {
                instance
                    ?: HiddenItemsRepository(hiddenItemsDao).also { instance = it }
            }
    }
}