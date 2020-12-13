package hu.droth.kristof.mindlearning.repository

import hu.droth.kristof.mindlearning.model.dao.PlayerDao
import hu.droth.kristof.mindlearning.model.entity.Player
import hu.droth.kristof.mindlearning.repository.BaseRepository
import javax.inject.Inject

class PlayerRepository @Inject constructor(private val playerDao: PlayerDao) : BaseRepository<Player>(playerDao) {

    suspend fun getPlayerByName(name: String): Player? = playerDao.getPlayerByName(name)

    suspend fun getPlayerById(id: Long): Player = playerDao.getPlayerById(id)

    suspend fun isExist(playerName: String) = playerDao.isExist(playerName)

    suspend fun getAllPlayer(): List<Player> = playerDao.getAllPlayer()
}