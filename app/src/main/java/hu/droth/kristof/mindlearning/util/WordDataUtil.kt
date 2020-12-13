
package hu.droth.kristof.mindlearning.util

import hu.droth.kristof.mindlearning.model.LanguageType
import hu.droth.kristof.mindlearning.model.WordTheme
import hu.droth.kristof.mindlearning.model.entity.Meaning
import hu.droth.kristof.mindlearning.model.entity.Word
import java.util.*

class WordDataUtil {
    companion object {
        fun setMeaningData(wordId: Long, languageType: LanguageType, writing: String): Meaning {
            val wordSize = writing.length
            val vowelSize = getVowelSize(writing)
            val consonantSize = wordSize - vowelSize
            return Meaning(
                wordId,
                languageType,
                writing,
                wordSize,
                vowelSize,
                consonantSize,
                vowelSize
            )
        }

        fun setWordData(theme: WordTheme): Word {
            val lastUpdateTime = Date(System.currentTimeMillis())
            return Word(
                lastUpdateTime = lastUpdateTime,
                theme = theme,
                imageUrl = null,
                blurImageUrl = null
            )
        }


        private fun getVowelSize(word: String): Int {
            var vowelSize = 0
            val lowerCaseWord = word.toLowerCase(Locale.getDefault())
            lowerCaseWord.forEach {
                if (it == 'a' ||
                    it == 'á' ||
                    it == 'e' ||
                    it == 'é' ||
                    it == 'i' ||
                    it == 'í' ||
                    it == 'o' ||
                    it == 'ó' ||
                    it == 'ö' ||
                    it == 'ő' ||
                    it == 'ü' ||
                    it == 'ű'
                )
                    vowelSize++
            }
            return vowelSize
        }

    }
}