package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Viktoria Chebotar on 21.04.18.
 */
@Singleton
class AddMeaningToFavoritesUseCase @Inject constructor(
    val userWordRepository: UserWordRepository,
    val userRepository: UserRepository
) {

    suspend fun execute(parameter: Parameter): Result<Nothing?> {
        val userResult = userRepository.getUser()
        return if (userResult is Result.Success) {
            val userWordChannel = userWordRepository.getUserWord(parameter.word)
            try {
                val userWordResult = userWordChannel.receive()
                addMeaningToFavorites(userWordResult, parameter)
            } catch (e: Exception) {
                Result.Error(e)
            }
        } else Result.Error((userResult as Result.Error).exception)
    }

    private suspend fun addMeaningToFavorites(
        userWordResult: Result<UserWord?>,
        parameter: Parameter
    ): Result<Nothing?> {
        return if (userWordResult is Result.Success) {
            userWordResult.data?.let {
                val meanings = it.favMeanings.toMutableSet()
                meanings.addAll(parameter.favMeaningIds)
                userWordRepository.addOrUpdateUserWord(UserWord(it.word, meanings.toList()))
            } ?: Result.Error(Exception())
        } else Result.Error((userWordResult as Result.Error).exception)
    }

    data class Parameter(val word: String, val favMeaningIds: List<String>)
}
