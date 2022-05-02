package uk.ac.aber.dcs.rco1.converser.model.home

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Database access object contains methods to query the database
 * Room generates an implementation of this DAO when referenced by converser room db
 *
 * @author Ruby Osborne (rco1)
 * @version 1.0 (release)
 */
@Dao
interface TranslationItemDao {

    /**
     * Inserts a translation item into the db
     *
     * @param translationItem - the translation item to enter into the db
     */
    @Insert
    fun insertSingleTranslationItem(translationItem: TranslationItem)

    /**
     * Deletes all translation items from the db
     *
     */
    @Query("DELETE FROM translationItems")
    fun deleteAll()

    /**
     * Gets all translation items from the db
     *
     * @return the list of translation items as live data
     */
    @Query("SELECT * FROM translationItems")
    fun getAllTranslationItems(): LiveData<List<TranslationItem>>


    //TODO: is this required for later work?
    /* @Query("SELECT * FROM translationItems WHERE language = 'A'")
     fun getLanguageAMessages(): LiveData<TranslationItem>

     @Query("SELECT * FROM translationItems WHERE language = 'B'")
     fun getLanguageBMessages(): LiveData<TranslationItem>*/

}