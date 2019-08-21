package com.sammengistu.stuckapp.utils

import android.content.Context
import com.sammengistu.stuckapp.data.AppDatabase
import com.sammengistu.stuckapp.data.PostRepository

object InjectorUtils {

    private fun getPostRepository(context: Context): PostRepository {
        return PostRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).postsDao()
        )
    }
//
//    fun providePlantListViewModelFactory(context: Context): PlantListViewModelFactory {
//        val repository = getPlantRepository(context)
//        return PlantListViewModelFactory(repository)
//    }
//
//    fun providePlantDetailViewModelFactory(
//        context: Context,
//        plantId: String
//    ): PlantDetailViewModelFactory {
//        return PlantDetailViewModelFactory(getPlantRepository(context),
//            getGardenPlantingRepository(context), plantId)
//    }
}