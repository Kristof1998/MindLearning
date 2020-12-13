package hu.droth.kristof.mindlearning.model.dao

import androidx.room.Dao
import androidx.room.Query
import hu.droth.kristof.mindlearning.model.entity.Player

@Dao
interface PlayerDao : BaseDao<Player> {

    @Query("SELECT * FROM players WHERE name = :name")
    suspend fun getPlayerByName(name: String): Player?

    @Query("SELECT * FROM players WHERE playerId=:id")
    suspend fun getPlayerById(id: Long): Player

    @Query("SELECT EXISTS (SELECT * FROM players WHERE name = :playerName)")
    suspend fun isExist(playerName: String): Boolean

    @Query("SELECT * FROM players")
    suspend fun getAllPlayer(): List<Player>
}