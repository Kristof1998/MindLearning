package hu.droth.kristof.mindlearning.api

import hu.droth.kristof.mindlearning.model.entity.PixaBayImage
import retrofit2.http.GET
import retrofit2.http.Query


interface ImageSearchApi {

    @GET("api/")
    suspend fun getImage(
        @Query("q") imageName: String,
        @Query("key") apiKey: String,
        @Query("per_page") resultPerPage: Int
    ): PixaBayImage
}