package hu.droth.kristof.mindlearning.repository

import hu.droth.kristof.mindlearning.model.dao.MeaningDao
import hu.droth.kristof.mindlearning.model.entity.Meaning
import hu.droth.kristof.mindlearning.repository.BaseRepository
import javax.inject.Inject

class MeaningRepository @Inject constructor(meaningDao: MeaningDao) : BaseRepository<Meaning>(meaningDao)