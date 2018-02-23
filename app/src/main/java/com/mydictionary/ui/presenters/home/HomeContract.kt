package com.mydictionary.ui.presenters.home

import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

interface HomePresenter : BasePresenter<HomeView> {
    fun onWordOfTheDayClicked()
    fun onResume()
    fun onWordOfTheDayFavoriteBtnClicked()
    fun onMyWordsBtnClicked()
}

interface HomeView : BaseView {
    fun showWordOfTheDay(word: WordDetails)
    fun showWordOfTheDayFavoriteBtnState(isFavorite: Boolean)
    fun startWordInfoActivity(word: WordDetails)
    fun startMyWordsActivity()
}

