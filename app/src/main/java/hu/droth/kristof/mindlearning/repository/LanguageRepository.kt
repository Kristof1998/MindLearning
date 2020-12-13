package hu.droth.kristof.mindlearning.repository

import hu.droth.kristof.mindlearning.model.dao.LanguageDao
import hu.droth.kristof.mindlearning.model.entity.Language
import hu.droth.kristof.mindlearning.repository.BaseRepository
import javax.inject.Inject

class LanguageRepository @Inject constructor(languageDao: LanguageDao) : BaseRepository<Language>(languageDao)