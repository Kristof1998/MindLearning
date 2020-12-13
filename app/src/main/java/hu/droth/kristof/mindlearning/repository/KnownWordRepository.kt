package hu.droth.kristof.mindlearning.repository

import hu.droth.kristof.mindlearning.model.dao.KnownWordDao
import hu.droth.kristof.mindlearning.model.entity.KnownWord
import hu.droth.kristof.mindlearning.repository.BaseRepository
import javax.inject.Inject

class KnownWordRepository @Inject constructor(private val knownWordDao: KnownWordDao) : BaseRepository<KnownWord>(knownWordDao) {


    suspend fun getAllKnownWordsByPlayerId(playerId: Long): List<KnownWord> = knownWordDao.getAllKnownWordsByPlayerId(playerId)

    suspend fun deleteKnownWordByWordIdAndPlayerId(wordId: Long, playerId: Long) = knownWordDao.deleteKnownWordByWordIdAndPlayerId(wordId, playerId)
}