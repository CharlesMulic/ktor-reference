package com.example.ktorapivideo.repo

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import ktorapivideo.errorAware
import ktorapivideo.respondSuccessJson

object PersonHandler {

    fun getPersonById(): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit {
        return {
            errorAware {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
                call.respond(PersonRepo.get(id))
            }
        }
    }

    fun getAllPersons(): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit {
        return {
            errorAware {
                call.respond(PersonRepo.getAll())
            }
        }
    }

    fun deletePersonById(): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit {
        return {
            errorAware {
                val id = call.parameters["id"] ?: throw IllegalArgumentException("Parameter id not found")
                call.respondSuccessJson(PersonRepo.remove(id))
            }
        }
    }

    fun deleteAllPersons(): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit {
        return {
            errorAware {
                PersonRepo.clear()
                call.respondSuccessJson()
            }
        }
    }

    fun addPerson(): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit {
        return {
            errorAware {
                val receive = call.receive<Person>()
                println("Received Post Request: $receive")
                call.respond(PersonRepo.add(receive))
            }
        }
    }
}