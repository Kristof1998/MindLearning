package hu.droth.kristof.mindlearning.model

import android.content.Context
import hu.droth.kristof.mindlearning.api.ImageSearchApi
import hu.droth.kristof.mindlearning.api.RetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.droth.kristof.mindlearning.model.dao.*
import hu.droth.kristof.mindlearning.repository.*


@InstallIn(SingletonComponent::class)
@Module
object DBModule {
    @Provides
    fun providePlayerDao(@ApplicationContext applicationContext: Context): PlayerDao {
        return AppDatabase(applicationContext).playerDao()
    }

    @Provides
    fun provideWordDao(@ApplicationContext applicationContext: Context): WordDao {
        return AppDatabase(applicationContext).wordDao()
    }

    @Provides
    fun provideMeaningDao(@ApplicationContext applicationContext: Context): MeaningDao {
        return AppDatabase(applicationContext).meaningDao()
    }

    @Provides
    fun provideLanguageDao(@ApplicationContext applicationContext: Context): LanguageDao {
        return AppDatabase(applicationContext).languageDao()
    }

    @Provides
    fun provideSessionDao(@ApplicationContext applicationContext: Context): SessionDao {
        return AppDatabase(applicationContext).sessionDao()
    }

    @Provides
    fun provideKnowWordDao(@ApplicationContext applicationContext: Context): KnownWordDao {
        return AppDatabase(applicationContext).knownWordDao()
    }

    @Provides
    fun provideFillDao(@ApplicationContext applicationContext: Context): FillDao {
        return AppDatabase(applicationContext).fillDao()
    }

    @Provides
    fun providePlayerRepository(playerDao: PlayerDao) = PlayerRepository(playerDao)

    @Provides
    fun provideWordRepository(wordDao: WordDao) = WordRepository(wordDao)

    @Provides
    fun provideLanguageRepository(languageDao: LanguageDao) = LanguageRepository(languageDao)

    @Provides
    fun provideMeaningRepository(meaningDao: MeaningDao) = MeaningRepository(meaningDao)

    @Provides
    fun provideSessionRepository(sessionDao: SessionDao) = SessionRepository(sessionDao)

    @Provides
    fun provideKnownWordRepository(knownWordDao: KnownWordDao) = KnownWordRepository(knownWordDao)

    @Provides
    fun provideFillRepository(fillDao: FillDao) = FillRepository(fillDao)

    @Provides
    fun provideImageSearchApi(): ImageSearchApi = RetrofitService.imageSearchApi


}