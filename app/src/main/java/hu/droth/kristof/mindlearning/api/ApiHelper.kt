package hu.droth.kristof.mindlearning.api

import javax.inject.Inject

class ApiHelper @Inject constructor(private val imageSearchApi: ImageSearchApi) {
    private val apiKey = "19124728-a783839fe887ede02c2f518d1"
    suspend fun getImage(imageName: String) = imageSearchApi.getImage(imageName, apiKey, 3)
}