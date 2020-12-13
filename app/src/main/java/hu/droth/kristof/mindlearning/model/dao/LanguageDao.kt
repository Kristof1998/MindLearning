package hu.droth.kristof.mindlearning.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import hu.droth.kristof.mindlearning.model.entity.Language


@Dao
interface LanguageDao : BaseDao<Language> {

    @Query("SELECT * FROM language")
    fun getAll(): LiveData<List<Language>>
}