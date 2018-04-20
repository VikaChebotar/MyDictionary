package com.mydictionary.data.repository

import com.mydictionary.data.firebasestorage.dto.WordList
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.SortingOption
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

interface AllRepository {
    fun loginFirebaseUser(googleToken: String?): Single<String>

    fun isSignedIn(): Single<Boolean>

    fun getUserName(): Single<String>

    fun signOut(): Completable

    fun getWordInfo(wordName: String): Single<WordDetails>

    fun getHistoryWords(): Flowable<List<String>>

    fun searchWord(searchPhrase: String): Single<List<String>>

    fun setWordFavoriteState(word: WordDetails, favMeanings: List<String>): Single<WordDetails>

    fun getFavoriteWordsInfo(
        offset: Int,
        pageSize: Int,
        sortingOption: SortingOption = SortingOption.BY_DATE
    ): Flowable<PagedResult<WordDetails>>

    fun getFavoriteWords(): Single<List<String>>

    fun getAllWordLists(): Single<List<WordList>>

    fun getWordList(wordListName: String): Single<List<String>>
}