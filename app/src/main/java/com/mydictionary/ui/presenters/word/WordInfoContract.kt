package com.mydictionary.ui.presenters.word

import android.os.Bundle
import com.mydictionary.data.entity.WordInfo
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria_Chebotar on 6/30/2017.
 */

interface WordInfoPresenter : BasePresenter<WordInfoView> {

}

interface WordInfoView : BaseView {
    fun getExtras(): Bundle
    fun initToolbar(word: String)
    fun bindWordInfo(wordInfo: WordInfo)
}