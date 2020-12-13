package hu.droth.kristof.mindlearning.model.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.entity.Fill

@Dao
interface FillDao : BaseDao<Fill> {

    @Transaction
    @Query("SELECT * FROM fills WHERE sessionId =:currentSessionId AND wordTheme =:wordTheme AND playerId =:playerId")
    suspend fun getFilledDataByCurrentSessionIdAndThemeAndPlayerId(
        currentSessionId: Long,
        wordTheme: WordTheme,
        playerId: Long
    ): List<Fill>

    @Query("SELECT * FROM fills WHERE playerId =:playerId")
    suspend fun getAllFillByPlayerId(playerId: Long): List<Fill>

    @Query("SELECT * FROM fills")
    suspend fun getAllFill(): List<Fill>
}