package hu.droth.kristof.mindlearning.model.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import hu.droth.kristof.mindlearning.model.entity.KnownWord

@Dao
interface KnownWordDao : BaseDao<KnownWord> {


    @Transaction
    @Query("SELECT * FROM known_words WHERE playerId =:playerId")
    suspend fun getAllKnownWordsByPlayerId(playerId: Long): List<KnownWord>

    @Query("DELETE FROM known_words WHERE knownWordId=:wordId AND playerId=:playerId")
    suspend fun deleteKnownWordByWordIdAndPlayerId(wordId: Long, playerId: Long)

}