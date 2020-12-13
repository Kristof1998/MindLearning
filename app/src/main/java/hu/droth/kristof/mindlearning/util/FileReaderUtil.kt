package hu.droth.kristof.mindlearning.util

import android.content.Context
import androidx.annotation.RawRes
import androidx.annotation.WorkerThread
import hu.droth.kristof.mindlearning.model.entity.Language
import hu.droth.kristof.mindlearning.model.helperClasses.ReaderWordData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FileReaderUtil @Inject constructor(private val applicationContext: Context) {

    @WorkerThread
    suspend fun readInitializeDataFromFile(@RawRes fileResourceId: Int): String {
        return withContext(Dispatchers.IO) {
            applicationContext.resources.openRawResource(fileResourceId)
                .bufferedReader().use { it.readText() }
        }
    }

    @WorkerThread
    suspend fun parseDataToReaderWordDataList(data: String): List<ReaderWordData> {
        return withContext(Dispatchers.IO) {
            val gson = Gson()
            val itemType = object : TypeToken<List<ReaderWordData>>() {}.type
            val returnData: List<ReaderWordData> = gson.fromJson(data, itemType)
            returnData
        }
    }


    fun parseDataToLanguageList(data: String): List<Language> {
        val gson = Gson()
        val itemType = object : TypeToken<List<Language>>() {}.type
        return gson.fromJson(data, itemType)
    }
}

