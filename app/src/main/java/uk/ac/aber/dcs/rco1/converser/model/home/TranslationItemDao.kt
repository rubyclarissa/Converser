package uk.ac.aber.dcs.rco1.converser.model.home

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TranslationItemDao {
    @Insert
    fun insertSingleTranslationItem(translationItem: TranslationItem)

    @Query("DELETE FROM translationItems")
    fun deleteAll()

    @Query("SELECT * FROM translationItems")
    fun getAllTranslationItems(): LiveData<List<TranslationItem>>

    @Query("SELECT * FROM translationItems WHERE language = 'A'")
    fun getLanguageAMessages(): LiveData<TranslationItem>

    @Query("SELECT * FROM translationItems WHERE language = 'B'")
    fun getLanguageBMessages(): LiveData<TranslationItem>

}