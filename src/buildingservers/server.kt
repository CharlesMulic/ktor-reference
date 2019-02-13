package com.example.buildingservers

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import ktorapivideo.main

// https://www.youtube.com/watch?v=V4PS3IjIzlw

// easy to use, fun, and asynchronous
// composable, DSL based web services in Kotlin

// main actor is Application object, accepts requests from server engine, returns responses

// when you build a Ktor app, you first set up the engine(s) (Jetty, Netty, Tomcat, Servlet, etc.)
// Standard servlet allows for auto scaling environments like Google App Engine

// You create a Ktor application by installing features that add functionality to the server
//  - Routing, Serialization, Templates, CORS, DefaultHeaders, ContentNegotiation, Auth, etc.

fun Application.verify() {
    routing {
        post("/verify") {
            call.respond("Hello World")
        }
    }
}

fun main() {
//    embeddedServer(Netty, 8080, module = Application::main).start(wait = true)
    embeddedServer(Netty, 8080, module = Application::verify).start(wait = true)
}