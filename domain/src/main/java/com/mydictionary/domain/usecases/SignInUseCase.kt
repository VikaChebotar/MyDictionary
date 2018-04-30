package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.User
import com.mydictionary.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInUseCase @Inject constructor(
    val userRepository: UserRepository) {

    suspend fun execute(parameter: String): Result<User> {
        val userResult = userRepository.getUser()
        return when (userResult) {
            is Result.Success<User> -> userResult
            is Result.Error -> userRepository.signIn(parameter)
        }
    }
}