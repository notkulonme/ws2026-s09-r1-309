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
    val json = Json { prettyPrint=true }
    val client = HttpClient(OkHttp)
    val url = environment.config.property("api.database_url").getString()
    routing {

        get("/customers/count") {
            val response = processGet(client, url)
            if (response == null){
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            call.respond(mapOf("count" to customerList.size))
        }

        get("/customers/avg-age"){
            val response = processGet(client, url)
            if (response == null){
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)

            val avgAge = customerList.mapNotNull { it.age }.average()
            call.respond(mapOf("avgAge" to avgAge))
        }

    }
}

suspend fun processGet(client: HttpClient, url:String):String?{
    val response = client.get(url)
    if (response.status != HttpStatusCode.OK)
        return null
    return response.body<String>().replace(": \"\"",": null")
}
