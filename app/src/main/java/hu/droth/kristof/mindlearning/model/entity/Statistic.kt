package hu.droth.kristof.mindlearning.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import hu.droth.kristof.mindlearning.model.entity.Player

@Entity(
    tableName = "statistics",
    foreignKeys = [ForeignKey(
        entity = Player::class,
        parentColumns = arrayOf("playerId"),
        childColumns = arrayOf("playerId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Statistic(
    @ColumnInfo(index = true)
    val playerId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var statisticId: Long = 0
}