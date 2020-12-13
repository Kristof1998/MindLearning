package hu.droth.kristof.mindlearning.model.helperClasses

import hu.droth.kristof.mindlearning.model.LanguageType
import hu.droth.kristof.mindlearning.model.WordTheme

data class ReaderWordData(
    val wordTheme: WordTheme,
    val meanings: Map<LanguageType, String>
)
