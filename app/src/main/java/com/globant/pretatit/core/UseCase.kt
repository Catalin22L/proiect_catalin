package com.globant.pretatit.core

interface UseCase<in I, out R> {

    suspend operator fun invoke(params: I): Result<R, Failure>
}