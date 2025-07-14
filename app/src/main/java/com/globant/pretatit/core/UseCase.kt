package com.globant.pretatit.core

interface UseCase<in I, out R> {

    operator fun invoke(params: I): Result<R, Failure>
}