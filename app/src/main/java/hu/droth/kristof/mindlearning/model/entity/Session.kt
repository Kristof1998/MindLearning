package hu.droth.kristof.mindlearning.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.LanguageType
import hu.droth.kristof.mindlearning.model.WordTheme
import java.util.*

@Entity(
    tableName = "sessions",
    foreignKeys = [ForeignKey(
        parentColumns = arrayOf("wordId"),
        childColumns = arrayOf("lastWordId"),
        entity = Word::class,
        onDelete = ForeignKey.SET_DEFAULT,
        onUpdate = ForeignKey.CASCADE
    )]
)

data class Session(
    val intelligenceType: IntelligenceType,
    val languageType: LanguageType,
    val wordTheme: WordTheme,
    @ColumnInfo(index = true)
    var lastWordId: Long?,
    val session: Int,
    var progress: Int = 0,
    var inProgress: Boolean,
    var lastModified: Date,
    @ColumnInfo(name = "session_player_id")
    val playerId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var sessionId: Long = 0

}
