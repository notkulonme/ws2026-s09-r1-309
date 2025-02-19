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
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import org.slf4j.LoggerFactory

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }

    val json = Json { prettyPrint = true }
    val client = HttpClient(OkHttp)
    val url = environment.config.property("api.database_url").getString()


    routing {

        get("/customers/count") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            call.respond(mapOf("count" to customerList.size))
        }

        get("/customers/avg-age") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)

            val avgAge = customerList.mapNotNull { it.age }.average()
            call.respond(mapOf("avgAge" to avgAge))
        }
        get("/customers/most-frequent-purchase-category") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            val category = customerList
                .mapNotNull { it.preferredCategory }
                .groupingBy { it }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key
            call.respond(mapOf("preferedCategory" to category))
        }

        get("/customers/sum-of-purchase") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            val sumOfPurchase = customerList.sumOf { it.totalSpending }.toLong()
            call.respond(mapOf("sumOfPurchase" to sumOfPurchase))
        }

        get("/customers/avg-order-value") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            val averageValue = customerList.map { it.averageOrderValue }.average()
            call.respond(mapOf("avgOrderValue" to averageValue))
        }

        get("/customers/purchase-frequency") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            val averageValue = customerList.map { it.frequency }.average()
            call.respond(mapOf("frequency" to averageValue))
        }

        get("/customers/gender-dist") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            val genderDistribution = customerList
                .mapNotNull { it.gender }
                .groupingBy { it }
                .eachCount()
                .toList()
                .map {
                    JsonArray(
                        listOf(
                            Json.encodeToJsonElement(
                                when (it.first) {
                                    "F" -> "Female"
                                    "M" -> "Male"
                                    else -> "error"
                                }
                            ), Json.encodeToJsonElement(it.second)
                        )
                    )
                }
                .toMutableList()

            genderDistribution.add(
                0,
                JsonArray(listOf(Json.encodeToJsonElement("Gender"), Json.encodeToJsonElement("Count")))
            )



            call.respond(mapOf("genderDist" to genderDistribution))
        }

        get("/customers/membership-dist") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            val membershipDistribution = customerList
                .mapNotNull { it.membership }
                .groupingBy { it }
                .eachCount()
                .toList()
                .map { JsonArray(listOf(Json.encodeToJsonElement(it.first), Json.encodeToJsonElement(it.second))) }
                .toMutableList()

            membershipDistribution.add(
                0,
                JsonArray(listOf(Json.encodeToJsonElement("Level"), Json.encodeToJsonElement("Count")))
            )

            call.respond(mapOf("membershipDist" to membershipDistribution))
        }

        get("/customers/categories") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            val categories = customerList
                .mapNotNull { it.preferredCategory }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .map { JsonArray(listOf(Json.encodeToJsonElement(it.first), Json.encodeToJsonElement(it.second))) }
                .toMutableList()

            categories.add(
                0,
                JsonArray(listOf(Json.encodeToJsonElement("Name"), Json.encodeToJsonElement("Count")))
            )

            call.respond(mapOf("categories" to categories))
        }

        get("/customers/top-spenders") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            val topSpenders = customerList
                .map { "${it.firstName} ${it.lastName}" to it.totalSpending }
                .sortedByDescending { it.second }
                .take(10)
                .map { JsonArray(listOf(Json.encodeToJsonElement(it.first), Json.encodeToJsonElement(it.second))) }
                .toMutableList()
            topSpenders.add(
                0,
                JsonArray(listOf(Json.encodeToJsonElement("Name"), Json.encodeToJsonElement("Spent")))
                )
            call.respond(mapOf("topSpenders" to topSpenders))
        }

        get("customers/trends") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            val trends = customerList
                .mapNotNull { it.joinedAt?.split("-")?.get(0)?.toInt() }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedBy { it.first }
                .map { JsonArray(listOf(Json.encodeToJsonElement(it.first), Json.encodeToJsonElement(it.second))) }
                .toMutableList()
            trends.add(0, JsonArray(listOf(Json.encodeToJsonElement("Years"), Json.encodeToJsonElement("New Customers"))))
            call.respond(mapOf("trends" to trends))
        }

        get("/customers") {
            val response = processGet(client, url)
            if (response == null) {
                call.respond(HttpStatusCode.InternalServerError, "Database error")
                return@get
            }
            val customerList = json.decodeFromString<ArrayList<Customer>>(response)
            call.respond(mapOf("customers" to customerList))
        }

    }
}

suspend fun processGet(client: HttpClient, url: String): String? {
    val log = LoggerFactory.getLogger("apiLogger")
    try {
        val response = client.get(url)
        if (response.status != HttpStatusCode.OK)
            return null
        return response.body<String>().replace(": \"\"", ": null")
    } catch (e: Exception) {
        log.error("database is not running")
        return null
    }

}
