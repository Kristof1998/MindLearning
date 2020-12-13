package hu.droth.kristof.mindlearning.model.entity

import androidx.room.*
import java.util.*

@Entity(
    tableName = "fills",
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

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED, RoomWarnings.INDEX_FROM_EMBEDDED_FIELD_IS_DROPPED, RoomWarnings.INDEX_FROM_EMBEDDED_ENTITY_IS_DROPPED)
data class Fill(
    @ColumnInfo(index = true)
    val playerId: Long,
    @ColumnInfo(index = true)
    val wordId: Long,
    val fillTime: Date,
    val correct: Boolean,
    @Embedded
    val session: Session
) {
    @PrimaryKey(autoGenerate = true)
    var fillId: Long = 0
}
