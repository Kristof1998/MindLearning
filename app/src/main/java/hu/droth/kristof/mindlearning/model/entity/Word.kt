package hu.droth.kristof.mindlearning.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.droth.kristof.mindlearning.model.WordTheme
import com.google.gson.annotations.Expose
import java.util.*

@Entity(tableName = "words")
data class Word(
    @Expose
    val lastUpdateTime: Date,
    val theme: WordTheme,
    @Expose
    var imageUrl: String? = null,
    @Expose
    var blurImageUrl: String? = null
) {
    @Expose
    @PrimaryKey(autoGenerate = true)
    var wordId: Long = 0
}