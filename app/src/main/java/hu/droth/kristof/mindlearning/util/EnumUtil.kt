package hu.droth.kristof.mindlearning.util

import hu.droth.kristof.mindlearning.R
import hu.droth.kristof.mindlearning.model.Gender
import hu.droth.kristof.mindlearning.model.IntelligenceType
import hu.droth.kristof.mindlearning.model.LanguageType
import hu.droth.kristof.mindlearning.model.WordTheme


fun Gender.getStringResource(): Int {
    return when (this) {
        Gender.MALE -> R.string.male
        Gender.FEMALE -> R.string.female
    }
}

fun WordTheme.getStringResource(): Int {
    return when (this) {
        WordTheme.ANIMAL -> R.string.animal
        WordTheme.ART -> R.string.art
        WordTheme.CLOTHING -> R.string.clothing
        WordTheme.COLOR -> R.string.color
        WordTheme.FAMILY -> R.string.family
        WordTheme.HUMAN_BODY -> R.string.human_body
        WordTheme.JOB -> R.string.job
        WordTheme.MUSIC -> R.string.music
        WordTheme.SCHOOL -> R.string.school
        WordTheme.WEATHER -> R.string.weather
    }
}

fun IntelligenceType.getStringResource(): Int {
    return when (this) {
        IntelligenceType.VERBAL -> R.string.verbal
        IntelligenceType.LOGICAL -> R.string.logical
        IntelligenceType.VISUAL -> R.string.visual
        IntelligenceType.MUSICAL -> R.string.musical
        IntelligenceType.VISUAL_HARD -> R.string.visual_hard
        IntelligenceType.TEST -> R.string.test
        IntelligenceType.RECOMMENDED_LEARNING ->R.string.recommended_learning
    }
}

fun LanguageType.getStringResource(): Int {
    return when (this) {
        LanguageType.ENGLISH -> R.string.english
        LanguageType.HUNGARIAN -> R.string.hungarian
    }
}

fun IntelligenceType.getIconDrawable(): Int {
    return when (this) {
        IntelligenceType.VERBAL -> R.drawable.ic_intelligence_verbal
        IntelligenceType.LOGICAL -> R.drawable.ic_intelligence_logical
        IntelligenceType.VISUAL -> R.drawable.ic_intelligence_visual
        IntelligenceType.MUSICAL -> R.drawable.ic_intelligence_musical
        IntelligenceType.VISUAL_HARD -> R.drawable.ic_intelligence_visual
        IntelligenceType.TEST -> R.drawable.ic_intelligence_test
        IntelligenceType.RECOMMENDED_LEARNING -> R.drawable.ic_recommend_icon
    }
}

fun LanguageType.getFlagDrawable(): Int {
    return when (this) {
        LanguageType.ENGLISH -> R.drawable.ic_flag_english
        LanguageType.HUNGARIAN -> R.drawable.ic_flag_hungary
    }
}
