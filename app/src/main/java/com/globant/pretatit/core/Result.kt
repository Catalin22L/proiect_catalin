package com.globant.pretatit.core

sealed class Result<out R, out E> {
    data class Success<out R>(val success: R) : Result<R, Nothing>()
    data class Error<out E>(val error: E) : Result<Nothing, E>()

    fun handleResult(
        successFlow: (R) -> Any,
        errorFlow: (E) -> Any
    ) {
        when (this) {
            is Success -> successFlow(this.success)
            is Error -> errorFlow(this.error)
        }
    }
}

sealed class Failure {
    data object SomethingWentWrong : Failure()
}