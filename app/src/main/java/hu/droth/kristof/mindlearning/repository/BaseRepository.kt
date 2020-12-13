package hu.droth.kristof.mindlearning.repository

import hu.droth.kristof.mindlearning.model.dao.BaseDao

abstract class BaseRepository<T>(private val roomDao: BaseDao<T>) {

    open suspend fun insert(obj: T): Long = roomDao.insert(obj)

    open suspend fun insert(objList: List<T>): List<Long> = roomDao.insert(objList)

    open suspend fun update(obj: T): Int = roomDao.update(obj)

    open suspend fun delete(obj: T) = roomDao.delete(obj)
}