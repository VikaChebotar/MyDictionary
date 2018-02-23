package com.mydictionary.ui.views.word

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.mydictionary.R
import com.mydictionary.data.pojo.WordMeaning
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.word.WordInfoPresenterImpl
import com.mydictionary.ui.presenters.word.WordInfoView
import kotlinx.android.synthetic.main.definition_card.*
import kotlinx.android.synthetic.main.word_info_activity.*

/**
 * Created by Viktoria Chebotar on 28.06.17.
 */

class WordInfoActivity : AppCompatActivity(), WordInfoView {

    val presenter by lazy { WordInfoPresenterImpl(DictionaryApp.getInstance(this).repository, this) }
    val definitionsAdapter = DefinitionsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.word_info_activity);
        initList(meaningRecyclerView, definitionsAdapter)

        presenter.onStart(this)

//        seeAllDefinitionsBtn.setOnClickListener {
//            presenter.onSeeAllDefinitionsBtnClicked(definitionsAdapter.itemCount)
//        }
//        seeAllExamplesBtn.setOnClickListener {
//            presenter.onSeeAllExamplesBtnClicked(examplesAdapter.itemCount)
//        }
//        favoriteFab.setOnClickListener {
//            presenter.onFavoriteClicked()
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.word_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.pronounce -> {
                return true
            }
        }
        return false
    }

    private fun initList(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.isAutoMeasureEnabled = true
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }
//
//    override fun showIsFavorite(isFavorite: Boolean) {
//        val imageRes = if (isFavorite) R.drawable.ic_favorite_white_24dp else
//            R.drawable.ic_favorite_border_white_24dp
//        favoriteFab.setImageResource(imageRes)
//    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onStop()
    }

    override fun initToolbar(word: String) {
        setSupportActionBar(toolbar)
        toolbarLayout.title = word
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showPronunciation(value: String) {
        pronunciation.text = getString(R.string.prononcuation, value)
    }

    //
//    override fun showMeanings(value: List<Definition>, showSeeAllBtn: Boolean) {
//        definitionsAdapter.dataset = value
//        definitionsAdapter.notifyDataSetChanged()
//        if (value.isEmpty()) definitionCard.visibility = View.GONE
//        seeAllDefinitionsBtn.visibility = if (showSeeAllBtn) View.VISIBLE else View.GONE
//    }
    override fun showMeanings(value: List<WordMeaning>) {
        definitionsAdapter.dataset = value
        definitionsAdapter.notifyDataSetChanged()
    }

    override fun showProgress(progress: Boolean) {

    }

    override fun showError(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    override fun getExtras(): Bundle {
        return intent.extras
    }
}
