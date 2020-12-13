package hu.droth.kristof.mindlearning.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "known_words",
    foreignKeys = [ForeignKey(
        entity = Player::class,
        parentColumns = arrayOf("playerId"),
        childColumns = arrayOf("playerId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = Word::class,
            parentColumns = arrayOf("wordId"),
            childColumns = arrayOf("wordId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )]
)
data class KnownWord(
    @ColumnInfo(index = true)
    val wordId: Long,
    @ColumnInfo(index = true)
    val playerId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var knownWordId: Long = 0
}