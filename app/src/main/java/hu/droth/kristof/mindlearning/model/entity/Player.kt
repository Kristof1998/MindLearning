package hu.droth.kristof.mindlearning.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.droth.kristof.mindlearning.model.Gender

@Entity(tableName = "players")
data class Player(
    val name: String,
    val gender: Gender
) {
    @PrimaryKey(autoGenerate = true)
    var playerId: Long = 0
}