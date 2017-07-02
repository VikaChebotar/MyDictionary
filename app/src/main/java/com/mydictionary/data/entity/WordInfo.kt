package com.mydictionary.data.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Viktoria_Chebotar on 6/1/2017.
 */
data class WordInfo(val word: String, val pronunciation: String?) : Parcelable {
    var definitions = mutableListOf<Definition>()

    var examples = mutableListOf<String>()

    var synonyms = mutableListOf<String>()

    var antonyms = mutableListOf<String>()

    var also = mutableListOf<String>()

    var derivation = mutableListOf<String>()

    var typeOf = mutableListOf<String>()

    var hasTypes = mutableListOf<String>()

    var partOf = mutableListOf<String>()

    var hasParts = mutableListOf<String>()

    var substanceOf = mutableListOf<String>()

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<WordInfo> = object : Parcelable.Creator<WordInfo> {
            override fun createFromParcel(source: Parcel): WordInfo = WordInfo(source)
            override fun newArray(size: Int): Array<WordInfo?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(word)
        dest.writeString(pronunciation)
    }
}

data class Definition(val definition: String, val partOfSpeech: String) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Definition> = object : Parcelable.Creator<Definition> {
            override fun createFromParcel(source: Parcel): Definition = Definition(source)
            override fun newArray(size: Int): Array<Definition?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(definition)
        dest.writeString(partOfSpeech)
    }
}

class WordInfoResult {
    val definition: String? = null
    val partOfSpeech: String? = null
    val examples: List<String> = listOf()
    val synonyms: List<String> = listOf()
    val antonyms: List<String> = listOf()
    val also: List<String> = listOf()
    val derivation: List<String> = listOf()
    val typeOf: List<String> = listOf()
    val hasTypes: List<String> = listOf()
    val partOf: List<String> = listOf()
    val hasParts: List<String> = listOf()
    val substanceOf: List<String> = listOf()
}