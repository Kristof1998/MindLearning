package hu.droth.kristof.mindlearning.model.helperClasses

import androidx.room.Embedded
import androidx.room.Relation
import hu.droth.kristof.mindlearning.model.entity.Meaning
import hu.droth.kristof.mindlearning.model.entity.Word

data class WordWithMeanings(
    @Embedded
    val wordData: Word,
    @Relation(
        parentColumn = "wordId",
        entityColumn = "wordId",
        entity = Meaning::class
    )
    val meanings: List<Meaning>
)