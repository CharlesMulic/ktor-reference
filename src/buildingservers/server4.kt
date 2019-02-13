package com.example.buildingservers.server4

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

fun Application.verify() {
    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        gson { } // or moshi, jackson, etc
    }

    val api = TotallyRealApi()
    val db = InMemoryDatabase()

    routing {
        post("/verify") {
            val request = call.receive<Request>() // receive information out of the request
            // should throw an exception if request can't be deserialized

            // find valid subscription in db or remotely
            //   save to database
            // return subscription or 404
            val subscription = db.subscriptionByUserId(request.userId) ?: api.findSubscription(
                request.userId,
                request.packageName,
                request.productId,
                request.token
            )?.also {
                db.createSubscription(it)
            }

            if (subscription == null) {
                call.respond(HttpStatusCode.NotFound, "Subscription invalid.")
            } else {
                call.respond(subscription)
            }

            call.respond(request)
        }
    }
}

class InMemoryDatabase(
    var store: Map<String, Subscription> = mutableMapOf(Pair("test", Subscription(
        "test",
        "package",
        "productid",
        "token"
    )))
) {
    suspend fun subscriptionByUserId(userId: String): Subscription? {
        return store[userId]
    }

    suspend fun createSubscription(subscription: Subscription) {
        println("Saving subscription in database")
        Thread.sleep(250)
        store = store.plus(Pair(subscription.userId, subscription))
        println("Subscription saved")
    }

}

class TotallyRealApi {
    suspend fun findSubscription(userId: String, packageName: String, productId: String, token: String): Subscription? {
        println("Fetching subscription...")
        Thread.sleep(1000)
        println("Subscription found")
        return Subscription(
            "test",
            "package",
            "productid",
            "token"
        )
    }

}

data class Response(val status: String)

data class Request(val userId: String, val packageName: String, val productId: String, val token: String)

interface Api {
    suspend fun findSubscription(userId: String, packageName: String, productId: String, token: String): Subscription?
}

data class Subscription(val userId: String, val packageName: String, val productId: String, val token: String)

//class PlayStore() {
//    suspend fun findSubscription(userId: String, packageName: String, productId: String, token: String): Subscription? =
//        coroutineScope {
//            val response = async {
//
//            }
//            response.await().asSubscription(ownerId, token)
//        }
//}

fun main() {
    embeddedServer(Netty, 8080, module = Application::verify).start(wait = true)
}