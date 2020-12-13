package hu.droth.kristof.mindlearning.repository

import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.dao.SessionDao
import hu.droth.kristof.mindlearning.model.entity.Session
import hu.droth.kristof.mindlearning.model.helperClasses.InProgressCourse
import hu.droth.kristof.mindlearning.repository.BaseRepository
import javax.inject.Inject

class SessionRepository @Inject constructor(private val sessionDao: SessionDao) : BaseRepository<Session>(sessionDao) {

    suspend fun getCurrentSessionByThemeAndIntelligenceAndPlayerId(
        wordTheme: WordTheme,
        intelligenceType: IntelligenceType,
        playerId: Long
    ): Session? = sessionDao.getCurrentSessionByThemeAndIntelligenceAndPlayerId(wordTheme, intelligenceType, playerId)

    suspend fun getInProgressSessions(playerId: Long): List<InProgressCourse> = sessionDao.getInProgressSessions(playerId)
    suspend fun getSessionById(sessionId: Long) = sessionDao.getSessionById(sessionId)
    suspend fun getAllSession(): List<Session> = sessionDao.getAllSession()
    suspend fun getSessionByPlayerId(playerId: Long) = sessionDao.getSessionByPlayerId(playerId)

}