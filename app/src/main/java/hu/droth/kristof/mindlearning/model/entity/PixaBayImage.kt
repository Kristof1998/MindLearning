package hu.droth.kristof.mindlearning.model.entity

import com.google.gson.annotations.SerializedName

data class PixaBayImage(
    @SerializedName("hits")
    val imageData: List<ImageData>,
    val total: Int,
    val totalHits: Int
) {
    data class ImageData(
        val comments: Int,
        val downloads: Int,
        val favorites: Int,
        val id: Int,
        val imageHeight: Int,
        val imageSize: Int,
        val imageWidth: Int,
        val largeImageURL: String,
        val likes: Int,
        val pageURL: String,
        val previewHeight: Int,
        val previewURL: String,
        val previewWidth: Int,
        val tags: String,
        val type: String,
        val user: String,
        val userImageURL: String,
        val user_id: Int,
        val views: Int,
        val webformatHeight: Int,
        val webformatURL: String,
        val webformatWidth: Int
    )
}