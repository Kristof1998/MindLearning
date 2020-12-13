package hu.droth.kristof.mindlearning.repository

import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.dao.WordDao
import hu.droth.kristof.mindlearning.model.entity.Word
import hu.droth.kristof.mindlearning.model.helperClasses.WordWithMeanings
import hu.droth.kristof.mindlearning.repository.BaseRepository
import javax.inject.Inject

class WordRepository @Inject constructor(private val wordDao: WordDao) :
    BaseRepository<Word>(wordDao) {


    suspend fun getWordWithMeaningsByThemeSuspended(wordTheme: WordTheme): List<WordWithMeanings> =
        wordDao.getWordWithMeaningsByTheme(wordTheme)

    suspend fun geWordWithMeaningsByWordId(id: Long): WordWithMeanings = wordDao.getWordWithMeaningsByWordId(id)

    suspend fun getWordById(id: Long): Word? = wordDao.getWordById(id)

    suspend fun getAllWords() = wordDao.getAllWords()

}
