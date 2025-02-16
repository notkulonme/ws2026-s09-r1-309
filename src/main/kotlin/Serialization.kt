package hu.notkulonme

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
    val client = HttpClient(OkHttp)
    val url = environment.config.property("api.database_url").getString()

    routing {

        get("/customers/count") {
            val response = client.get(url)
            if (response.status != HttpStatusCode.OK) {
                call.respond(HttpStatusCode.InternalServerError, "database is not running")
                return@get
            }
            val customerList = Json.decodeFromString<ArrayList<Customer>>(response.body<String>())
            call.respond(mapOf("count" to customerList.size))
        }

        get("/customers/avg-age"){
            val response = client.get(url)
            if (response.status != HttpStatusCode.OK) {
                call.respond(HttpStatusCode.InternalServerError, "database is not running")
                return@get
            }
            val customerList = Json.decodeFromString<ArrayList<Customer>>(response.body<String>())

            val avgAge = customerList.mapNotNull { it.age }.average()
            call.respond(mapOf("avgAge" to avgAge))
        }
    }
}
