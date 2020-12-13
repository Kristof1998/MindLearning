package hu.droth.kristof.mindlearning.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.droth.kristof.mindlearning.model.LanguageType
import com.google.gson.annotations.Expose

@Entity
data class Language(
    val name: LanguageType,
    val shortName: String,
    val flagPath: String,
) {
    @Expose
    @PrimaryKey(autoGenerate = true)
    var languageId: Long = 0
}