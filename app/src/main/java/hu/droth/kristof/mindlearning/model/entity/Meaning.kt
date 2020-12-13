package hu.droth.kristof.mindlearning.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import hu.droth.kristof.mindlearning.model.LanguageType
import com.google.gson.annotations.Expose

@Entity(
    tableName = "meanings", foreignKeys = [ForeignKey(
        entity = Word::class,
        parentColumns = arrayOf("wordId"),
        childColumns = arrayOf("wordId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Meaning(
    @Expose
    @ColumnInfo(index = true)
    val wordId: Long,
    val languageType: LanguageType,
    val writing: String,
    @Expose
    val wordSize: Int,
    @Expose
    val vowelSize: Int,
    @Expose
    val consonantSize: Int,
    @Expose
    val syllable: Int
) {
    @Expose
    @PrimaryKey(autoGenerate = true)
    var meaningId: Long = 0
}