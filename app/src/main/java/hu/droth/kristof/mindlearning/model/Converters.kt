package hu.droth.kristof.mindlearning.model

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun wordThemeToString(wordTheme: WordTheme): String {
        return wordTheme.name
    }

    @TypeConverter
    fun stringToWordTheme(value: String): WordTheme {
        return WordTheme.valueOf(value)
    }

    @TypeConverter
    fun languageToString(languageType: LanguageType): String {
        return languageType.name
    }

    @TypeConverter
    fun stringToLanguage(value: String): LanguageType {
        return LanguageType.valueOf(value)
    }

    @TypeConverter
    fun genderToString(gender: Gender): String {
        return gender.name
    }

    @TypeConverter
    fun stringToGender(value: String): Gender {
        return Gender.valueOf(value)
    }

    @TypeConverter
    fun intelligenceTypeToString(intelligenceType: IntelligenceType): String {
        return intelligenceType.name
    }

    @TypeConverter
    fun stringToIntelligenceType(value: String): IntelligenceType {
        return IntelligenceType.valueOf(value)
    }
}