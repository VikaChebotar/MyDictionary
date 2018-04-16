package com.mydictionary.ui.views.learn

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.mydictionary.R
import com.mydictionary.commons.zipLiveData
import com.mydictionary.data.pojo.SortingOption
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.Data
import com.mydictionary.ui.DataState
import com.mydictionary.ui.DictionaryApp
import com.mydictionary.ui.presenters.learn.DeletedWordInfo
import com.mydictionary.ui.presenters.learn.LearnWordsViewModel
import com.mydictionary.ui.views.word.WordInfoActivity
import kotlinx.android.synthetic.main.learn_activity.*


/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
class LearnActivity : AppCompatActivity(), LearnCardItemFragment.OnCardItemListener,
    SortingDialogListener {
    private val viewModel by lazy {
        ViewModelProviders.of(
            this,
            DictionaryApp.getInstance(this).viewModelFactory
        )
            .get(LearnWordsViewModel::class.java)
    }
    val space by lazy { resources.getDimension(R.dimen.cards_view_pager_margin).toInt() }
    val adapter = LearnCardPagerAadapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.learn_activity);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        favWordsPager.adapter = adapter
        favWordsPager.pageMargin = space

        favWordsPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.onItemSelected(position)
            }
        })
        zipLiveData(
            viewModel.currentSelectedPosition,
            viewModel.totalSize
        )
            .observe(this, Observer { showPositionText(it?.first, it?.second) })
        viewModel.list.observe(this, Observer { showFavoriteWords(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.learn_cards_menu, menu)
        return true
    }

    fun showProgress(progress: Boolean) {
        progressBar.visibility = if (progress) View.VISIBLE else View.GONE
    }

    fun showError(message: String) {
        Snackbar.make(favWordsPager, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showPositionText(currentPosition: Int?, totalSize: Int?) {
        positionLabel.text = totalSize?.let {
            getString(
                R.string.fav_word_selected,
                favWordsPager.currentItem + 1, totalSize
            ) ?: ""
        }
    }

    private fun showFavoriteWords(data: Data<List<WordDetails>>?) {
        data?.let {
            showProgress(it.dataState == DataState.LOADING && (it.data == null || it.data.isEmpty()))
            it.data?.let {
                adapter.list = it
                adapter.notifyDataSetChanged()
            }
            it.message?.let { showError(it) }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_sort -> {
                showSortingDialog(viewModel.sortingType.value)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDetailsClicked(word: WordDetails) {
        WordInfoActivity.startActivity(this, word.word)
    }

    override fun onDeleteClicked(word: WordDetails) {
        viewModel.onItemDeleteClicked(word)
        viewModel.deletedWordInfo.observe(this, object : Observer<Data<DeletedWordInfo>> {
            override fun onChanged(t: Data<DeletedWordInfo>?) {
                viewModel.deletedWordInfo.removeObserver(this)
                handleWordDeletedResult(t)
            }
        })
    }

    private fun handleWordDeletedResult(data: Data<DeletedWordInfo>?) {
        data?.let {
            when (data.dataState) {
                DataState.SUCCESS -> data.data?.let { showWordDeletedMessage(it) }
                DataState.ERROR -> showError(data.message ?: getString(R.string.delete_word_error))
                DataState.LOADING -> {
                }
            }
        }
    }

    private fun showWordDeletedMessage(deletedWordInfo: DeletedWordInfo) {
        Snackbar.make(
            favWordsPager,
            getString(R.string.word_removed, deletedWordInfo.wordDetails.word),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo), {
                viewModel.onUndoDeletionClicked(deletedWordInfo)
            })
            .show();
    }

    override fun onSortItemSelected(item: SortingOption) {
        viewModel.onSortSelected(item)
    }

    private fun showSortingDialog(selectedSortingOption: SortingOption) {
        if (adapter.list.isEmpty()) return
        val dialog = LearnSortOptionsDialog.getInstance(selectedSortingOption)
        dialog.show(supportFragmentManager, LearnSortOptionsDialog::class.java.name)
        dialog.listener = this
    }
}