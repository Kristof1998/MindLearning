package hu.droth.kristof.mindlearning.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.entity.Session
import hu.droth.kristof.mindlearning.model.helperClasses.InProgressCourse

@Dao
interface SessionDao : BaseDao<Session> {

    @Query("SELECT * FROM sessions WHERE wordTheme = :wordTheme AND intelligenceType = :intelligenceType")
    fun getCurrentSessionByThemeAndIntelligence(
        wordTheme: WordTheme,
        intelligenceType: IntelligenceType
    ): LiveData<List<Session>>


    @Query("SELECT * FROM sessions WHERE wordTheme = :wordTheme AND intelligenceType = :intelligenceType AND session_player_id =:playerId ORDER BY lastModified DESC")
    suspend fun getCurrentSessionByThemeAndIntelligenceAndPlayerId(
        wordTheme: WordTheme,
        intelligenceType: IntelligenceType,
        playerId: Long
    ): Session?


    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM sessions WHERE inProgress LIKE 1 AND session_player_id=:playerId ORDER BY lastModified DESC")
    suspend fun getInProgressSessions(playerId: Long): List<InProgressCourse>

    @Query("SELECT * FROM sessions WHERE sessionId =:sessionId")
    suspend fun getSessionById(sessionId: Long): Session


    @Query("SELECT * FROM sessions WHERE session_player_id =:playerId")
    suspend fun getSessionByPlayerId(playerId: Long): List<Session>

    @Query("SELECT * FROM sessions")
    suspend fun getAllSession(): List<Session>


}