package com.mydictionary.data.repository

import com.mydictionary.commons.Constants
import com.mydictionary.data.entity.HistoryWord
import com.mydictionary.data.entity.WordOfTheDay
import io.realm.Realm
import io.realm.Sort
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */
class LocalStorage {
    //TODO where to close?
    private val realm: Realm = Realm.getDefaultInstance()

    fun getWordOfTheDay(): WordOfTheDay? {
        return realm.where(WordOfTheDay::class.java).findFirst();
    }

    fun storeWordOfTheDay(word: String, date: Date) {
        realm.executeTransactionAsync { realm: Realm? ->
            run {
                realm?.delete(WordOfTheDay::class.java)
                realm?.copyToRealm(WordOfTheDay(word, date))
            }
        }
    }

    fun getHistoryWords(limit: Int, listener: RepositoryListener<List<HistoryWord>>) {
        val results = realm.where(HistoryWord::class.java).findAllSortedAsync("accessTime", Sort.DESCENDING)
        results.addChangeListener { t -> listener.onSuccess(t.subList(0, Math.min(limit, t.size))) }
    }

    fun addWordToHistory(word: HistoryWord) {
        realm.executeTransactionAsync { realm ->
            if (realm.where(HistoryWord::class.java).count() >= Constants.MAX_HISTORY_LIMIT) {
                val oldestWord = realm.where(HistoryWord::class.java).
                        equalTo("isFavorite", false).
                        findAllSorted("accessTime", Sort.ASCENDING).first()
                oldestWord?.deleteFromRealm()
            }
            realm?.copyToRealmOrUpdate(word)
        }
    }

    fun getWordFromHistory(wordName: String): HistoryWord? {
        val realmWord = realm.where(HistoryWord::class.java).equalTo("word", wordName).findFirst()
        return if (realmWord != null) realm.copyFromRealm(realmWord) else null
    }

    fun isWordFavorite(wordName: String): Boolean {
        val historyWord = realm.where(HistoryWord::class.java).equalTo("word", wordName).findFirst()
        return historyWord?.isFavorite ?: false
    }

    fun setWordFavoriteState(wordName: String, isFavorite: Boolean): Boolean {
        val historyWord = realm.where(HistoryWord::class.java).equalTo("word", wordName).findFirst()
        realm.executeTransaction {
            historyWord?.isFavorite = isFavorite
        }
        return historyWord?.isFavorite ?: false
    }
}
