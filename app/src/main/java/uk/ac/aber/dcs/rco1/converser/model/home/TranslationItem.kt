package uk.ac.aber.dcs.rco1.converser.model.home

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TODO
 *
 */
@Entity(tableName = "translationItems")
data class TranslationItem(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0,
    val originalTranslationItem: String?,
    val translatedTranslationItem: String?,
    val language: Char?){}