package uk.ac.aber.dcs.rco1.converser.model.home

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A translation item within a conversation
 * Entity annotation allows there to be a table for translation items in the db
 * Primary key is the id which is automatically generated
 *
 * @author Ruby Osborne (rco1)
 * @version 1.0 (release)
 */
@Entity(tableName = "translationItems")
data class TranslationItem(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int = 0,
    val originalTranslationItem: String?,
    val translatedTranslationItem: String?,
    val language: PositionInConversation
) {}