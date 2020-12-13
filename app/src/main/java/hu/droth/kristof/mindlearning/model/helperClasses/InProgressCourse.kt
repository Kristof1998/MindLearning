package hu.droth.kristof.mindlearning.model.helperClasses

import androidx.room.ColumnInfo
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.WordTheme

data class InProgressCourse(
    @ColumnInfo(name = "wordTheme")
    val wordTheme: WordTheme,
    @ColumnInfo(name = "intelligenceType")
    val intelligenceType: IntelligenceType,
    @ColumnInfo(name = "progress")
    val progress: Int
)