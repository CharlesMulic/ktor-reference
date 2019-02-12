package ktorapivideo

import com.example.ktorapivideo.repo.PersonHandler
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.html.*
import java.text.DateFormat
import java.time.Duration

const val PERSON_ENDPOINT = "/person"

// https://www.youtube.com/watch?v=5sYhSrDTCls
// TODO validation errors
// TODO dependency injection
// TODO interface refactoring
// TODO unit and integration testing
fun Application.main() {
    install(DefaultHeaders)
    install(CORS) {
        maxAge = Duration.ofDays(1)
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    routing {
        get("$PERSON_ENDPOINT/{id}", PersonHandler.getPersonById())
        get("$PERSON_ENDPOINT/", PersonHandler.getAllPersons())
        delete("$PERSON_ENDPOINT/{id}", PersonHandler.deletePersonById())
        delete(PERSON_ENDPOINT, PersonHandler.deleteAllPersons())
        post(PERSON_ENDPOINT, PersonHandler.addPerson())

        get("/") {
            call.respondHtml {
                head {
                    title("Kotlin API example")
                }
                body {
                    div {
                        h1 {
                            +"Welcome to the Persons API"
                        }
                        p {
                            + "Go to '/person' to use the API"
                        }
                    }
                }
            }
        }
    }
}

suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respondText("""{"error": "$e"} """, ContentType.parse("application/json"), HttpStatusCode.BadRequest)
        null
    }
}

suspend fun ApplicationCall.respondSuccessJson(value: Boolean = true): Unit = respond("""{"success": "$value"}""")