package hu.droth.kristof.mindlearning.repository

import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.dao.FillDao
import hu.droth.kristof.mindlearning.model.entity.Fill
import hu.droth.kristof.mindlearning.repository.BaseRepository
import javax.inject.Inject

class FillRepository @Inject constructor(private val fillDao: FillDao) : BaseRepository<Fill>(fillDao) {

    suspend fun getFilledDataByCurrentSessionIdAndThemeAndPlayerId(
        currentSessionId: Long,
        wordTheme: WordTheme,
        playerId: Long
    ): List<Fill> = fillDao.getFilledDataByCurrentSessionIdAndThemeAndPlayerId(currentSessionId, wordTheme, playerId)

    suspend fun getAllFillByPlayerId(playerId: Long): List<Fill> = fillDao.getAllFillByPlayerId(playerId)
    suspend fun getAllFill(): List<Fill> = fillDao.getAllFill()
}