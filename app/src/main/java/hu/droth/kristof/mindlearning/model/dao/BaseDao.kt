package hu.droth.kristof.mindlearning.model.dao

import androidx.room.*


interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insert(objList: List<T>): List<Long>

    @Update
    suspend fun update(obj: T): Int

    @Delete
    suspend fun delete(obj: T)

}