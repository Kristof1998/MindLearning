package hu.droth.kristof.mindlearning.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.droth.kristof.mindlearning.model.dao.*
import hu.droth.kristof.mindlearning.model.entity.*

@Database(
    entities = [
        Fill::class,
        Meaning::class,
        Player::class,
        Session::class,
        Statistic::class,
        Word::class,
        KnownWord::class,
        Language::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun wordDao(): WordDao
    abstract fun fillDao(): FillDao
    abstract fun knownWordDao(): KnownWordDao
    abstract fun languageDao(): LanguageDao
    abstract fun meaningDao(): MeaningDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "appDatabase")
                .build()
    }
}