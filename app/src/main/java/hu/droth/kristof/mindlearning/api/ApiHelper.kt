package hu.droth.kristof.mindlearning.api

import hu.droth.kristof.mindlearning.BuildConfig
import javax.inject.Inject

class ApiHelper @Inject constructor(private val imageSearchApi: ImageSearchApi) {
    private val apiKey = BuildConfig.PIXABAYAPIKEY
    suspend fun getImage(imageName: String) = imageSearchApi.getImage(imageName, apiKey, 3)
}