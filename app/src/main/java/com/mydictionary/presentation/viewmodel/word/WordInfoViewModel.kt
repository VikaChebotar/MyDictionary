package com.mydictionary.presentation.viewmodel.word

import android.app.Application
import android.arch.lifecycle.*
import android.content.res.Resources
import android.util.Log
import com.mydictionary.R
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.pojo.WordMeaning
import com.mydictionary.data.repository.AllRepository
import com.mydictionary.presentation.Data
import com.mydictionary.presentation.DataState
import com.mydictionary.presentation.DictionaryApp
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WordInfoViewModel(
    private val repository: AllRepository,
    private val wordName: String,
    app: Application
) :
    AndroidViewModel(app) {
    private val wordInfo = MutableLiveData<Data<WordDetails>>()
    private val compositeDisposable = CompositeDisposable()
    val wordPresentationDetails: LiveData<Data<WordPresentationDetails>> =
        Transformations.map(wordInfo,
            {
                val presentationDetails =
                    mapToPresentation(it.data, getApplication<DictionaryApp>().resources)
                Data(it.dataState, presentationDetails, it.message)
            })

    init {
        Log.d("TAG", "word info view model")
        loadWordInfo()
    }

    private fun loadWordInfo() {
        compositeDisposable.add(
            Single.just(wordName)
                .doOnSubscribe {
                    wordInfo.postValue(Data(DataState.LOADING, wordInfo.value?.data, null))
                }
                .flatMap { repository.getWordInfo(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    wordInfo.value = Data(DataState.SUCCESS, it, null)
                }, { e ->
                    wordInfo.value = Data(DataState.ERROR, null, e.message)
                })
        )
    }

    fun onFavoriteClicked(item: WordMeaning) {
        wordInfo.value?.data?.let {
            compositeDisposable.add(
                Single.just(it)
                    .map {
                        val favMeanings = mutableListOf<String>()
                        it.meanings.filter { it.isFavourite }
                            .forEach { favMeanings.add(it.definitionId) }
                        if (favMeanings.contains(item.definitionId)) {
                            favMeanings.remove(item.definitionId)
                        } else favMeanings.add(item.definitionId)
                        favMeanings
                    }
                    .flatMap { favMeanings ->
                        repository.setWordFavoriteState(it, favMeanings)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { word ->
                            wordInfo.value = Data(DataState.SUCCESS, word, null)
                        },
                        { error ->
                            wordInfo.value = Data(DataState.ERROR, it, error.message)
                        })
            )
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}

private fun mapToPresentation(
    wordDetails: WordDetails?,
    resources: Resources
): WordPresentationDetails? {
    return wordDetails?.let {
        val wordCardsList = mutableListOf<Any>()
        wordCardsList.add(resources.getString(R.string.definitions) ?: "")
        wordCardsList.addAll(it.meanings)
        if (it.synonyms.isNotEmpty()) {
            wordCardsList.add(resources.getString(R.string.synonyms))
            wordCardsList.add(it.synonyms)
        }
        if (it.antonyms.isNotEmpty()) {
            wordCardsList.add(resources.getString(R.string.antonyms))
            wordCardsList.add(it.antonyms)
        }
        if (it.notes.isNotEmpty()) {
            wordCardsList.add(resources.getString(R.string.notes))
            wordCardsList.addAll(it.notes)
        }
        WordPresentationDetails(it.pronunciation, wordCardsList)
    }
}

data class WordPresentationDetails(val pronunciation: String? = null, val contentList: List<Any>)

class WordInfoViewModelFactory(
    private val wordName: String
) :
    ViewModelProvider.Factory {
    @Inject
    lateinit var repository: AllRepository
    @Inject
    lateinit var app: DictionaryApp

    init {
        DictionaryApp.component.inject(this)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            WordInfoViewModel::class.java -> WordInfoViewModel(repository, wordName, app) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class");
        }
    }
}