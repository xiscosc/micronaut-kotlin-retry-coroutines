package com.example.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.retry.annotation.Retryable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import javax.inject.Singleton

@Controller("/home")
class HomeController(val homeService: HomeService) {

    @Get("/1")
    // Cast error
    // io.micronaut.aop.util.CompletableFutureContinuation -> kotlin.coroutines.jvm.internal.CoroutineStackFrame
    suspend fun getTest1(): HttpResponse<String> {
        return HttpResponse.ok(homeService.test1())
    }

    @Get("/2")
    // Unexpected error occurred: Not a Kotlin coroutine
    suspend fun getTest2(): HttpResponse<String> {
        return HttpResponse.ok(homeService.test2())
    }

    @Get("/3")
    // Unexpected error occurred: Not a Kotlin coroutine
    suspend fun getTest3(): HttpResponse<String> {
        return HttpResponse.ok(homeService.test3())
    }

    @Get("/4")
    // It works
    suspend fun getTest4(): HttpResponse<String> {
        return HttpResponse.ok(homeService.test4())
    }

    @Get("/5")
    // Unexpected error occurred: Not a Kotlin coroutine
    suspend fun getTest5(): HttpResponse<Deferred<String>> {
        val result = coroutineScope {
            async {
                homeService.test2()
            }
        }
        return HttpResponse.ok(result)
    }
}

@Singleton
@Retryable(attempts = "1")
class HomeService {
    suspend fun test1(): String {
        return coroutineScope {
            delay(1L)
            "hi"
       }
    }

    suspend fun test2(): String {
        return coroutineScope {
            "hi"
        }
    }

    suspend fun test3(): String {
        return "hi"
    }

    suspend fun test4(): String {
        delay(1L)
        return "hi"
    }

}
