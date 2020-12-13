package hu.droth.kristof.mindlearning.model.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.entity.Word
import hu.droth.kristof.mindlearning.model.helperClasses.WordWithMeanings

@Dao
interface WordDao : BaseDao<Word> {

    @Transaction
    @Update
    suspend fun updateImageByListOfWord(wordList: List<Word>)

    @Transaction
    @Query("SELECT * FROM  words WHERE theme = :wordTheme")
    suspend fun getWordWithMeaningsByTheme(wordTheme: WordTheme): List<WordWithMeanings>

    @Transaction
    @Query("SELECT * FROM words WHERE wordId =:id")
    suspend fun getWordWithMeaningsByWordId(id: Long): WordWithMeanings

    @Query("SELECT * FROM words WHERE wordId =:id")
    suspend fun getWordById(id: Long): Word?

    @Transaction
    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<WordWithMeanings>


}