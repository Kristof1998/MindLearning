package hu.droth.kristof.mindlearning.repository

import android.content.res.Resources
import android.util.Log
import hu.droth.kristof.mindlearning.api.ApiHelper
import hu.droth.kristof.mindlearning.model.LanguageType
import hu.droth.kristof.mindlearning.model.helperClasses.WordWithMeanings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageSearchRepository @Inject constructor(private val apiHelper: ApiHelper) {


    suspend fun getImageUrlData(wordWithMeanings: WordWithMeanings, hitNumber: Int): Pair<Long, String?> {
        return withContext(Dispatchers.IO) {
            val meaning = wordWithMeanings.meanings.find { it.languageType == LanguageType.ENGLISH } ?: throw Resources.NotFoundException("ImageName with LanguageType.English not found")
            val imageName = meaning.writing
            val wordId = wordWithMeanings.wordData.wordId
            val resultData = apiHelper.getImage(imageName)
            return@withContext if (resultData.imageData.size > hitNumber) {//there is hit so we can add image to this file
                val imageUrl = resultData.imageData[hitNumber].webformatURL
                Log.d("IMAGE_SEARCH_REPOSITORY","ImageURL for $meaning :$imageUrl")
                Pair(wordId, imageUrl)
            } else {
                Log.d("IMAGE_SEARCH_REPOSITORY","ImageURL for $meaning :null")
                Pair(wordId, null)
            }

        }
    }


}