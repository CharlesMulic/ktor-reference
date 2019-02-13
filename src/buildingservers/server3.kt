package com.example.buildingservers.server3

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

fun Application.verify() {
    install(StatusPages) {
        exception<Throwable> { e ->
            call.respondText(e.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
    }

    install(ContentNegotiation) {
        gson { } // or moshi, jackson, etc
    }

    routing {
        post("/verify") {
            val request = call.receive<Request>() // receive information out of the request
            // should throw an exception if request can't be deserialized
            call.respond(request)
        }
    }
}

data class Response(val status: String)

data class Request(val userId: String, val packageName: String, val productId: String, val token: String)

fun main() {
    embeddedServer(Netty, 8080, module = Application::verify).start(wait = true)
}