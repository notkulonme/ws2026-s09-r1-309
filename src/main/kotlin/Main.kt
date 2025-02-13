package org.example

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File


@Serializable
data class Customer(
    val id: String,
    val firstName: String,
    val lastName: String,
    val age: Int?,
    val gender: String?,
    val postalCode: String,
    val email: String?,
    val phone: String?,
    val membership: String?,
    val joinedAt: String?,
    val lastPurchaseAt: String?,
    val totalSpending: Double,
    val averageOrderValue: Double,
    val frequency: Int,
    val preferredCategory: String?,
    val churned: Boolean?
)

data class ErrorData(
    val line: Int,
    val errors: ArrayList<String>
) {
    override fun toString(): String {
        if (errors.size > 1)
            return "$line,\"${errors.joinToString(",")}\"\n"
        else if (errors.size == 1)
            return "$line,${errors[0]}\n"
        else
            return ""
    }
}

@Serializable
data class CustomerList(
    val customers: ArrayList<Customer>
)

suspend fun main(args: Array<String>) {
    val customerList = ArrayList<Customer>()
    val errorList = StringBuilder("Row Number,Error Description\n")

    println("processing ${args[0]}")


    File("C:\\Users\\nrics\\Desktop\\ws2026-s09-hu-r1\\assets\\customers.csv").readLines().drop(1)
        .forEachIndexed { index, line ->

            val errorData = ErrorData(index + 2, ArrayList<String>())

            val elements = line.split(Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))//checks if the , char is not between "
            val id = elements[0]
            val firstName = elements[1]
            val lastName = elements[2]
            val age = elements[3].toDoubleOrNull()?.toInt()
            val gender =
                if (elements[4] == "M" || elements[4] == "F") {
                    elements[4]
                } else {
                    errorData.errors.add("Invalid gender")
                    null
                }
            val postalCode = elements[5]
            val email =
                if (elements[6].contains("@")) {
                    if (elements[6].split("@")[1].contains("."))
                        elements[6]
                    else {
                        errorData.errors.add("Invalid email format")
                        ""
                    }
                } else {
                    errorData.errors.add("Invalid email format")
                    ""
                }
            val phone =
                if (elements[7].replace(Regex("[^0-9]"), "").length >= 10) {
                    elements[7].replace(Regex("[^0-9]"), "")
                } else {
                    errorData.errors.add("Phone number too short")
                    null
                }
            val membership =
                when (elements[8].lowercase()) {
                    "basic" -> "bronze"
                    "silver" -> "silver"
                    "gold" -> "gold"
                    else -> {
                        errorData.errors.add("Invalid membership value")
                        null
                    }
                }

            val joinedAt = validateDates(elements[9], errorData.errors)
            var lastPurchaseAt = validateDates(elements[10], errorData.errors)
            if (lastPurchaseAt != null && joinedAt != null) {
                if (lastPurchaseAt.split("-")[0].toInt() !in 2000..2025)
                    errorData.errors.add("lastPurchaseAt is out of date range")
                if (joinedAt.split("-")[0].toInt() !in 2000..2025)
                    errorData.errors.add("joinedAt is out of date range")

                if (lastPurchaseAt.split("-")[0].toInt() < joinedAt.split("-")[0].toInt()) {
                    errorData.errors.add("Last purchase date earlier than join date")
                    lastPurchaseAt = null
                }
            }
            val totalSpending = elements[11].toDouble()
            val averageOrderValue = elements[12].toDouble()
            val frequency = elements[13].toInt()
            val preferredCategory = when (elements[14]) {
                "Unknown", "TBD", "To Be Determined", "N/A" -> {
                    errorData.errors.add("Invalid preferredCategory")
                    null
                }

                else -> elements[14]
            }
            val churned = when (elements[15]) {
                "Y", "yes", "1" -> true
                "N", "no", "2" -> false
                else -> {
                    errorData.errors.add("churned is not a boolean")
                    null
                }
            }
            if (errorData.errors.size != 0)
                errorList.append(errorData.toString())
            customerList.add(
                Customer(
                    id = id,
                    firstName = firstName,
                    lastName = lastName,
                    age = age,
                    gender = gender,
                    postalCode = postalCode,
                    email = email,
                    phone = phone,
                    membership = membership,
                    joinedAt = joinedAt,
                    lastPurchaseAt = lastPurchaseAt,
                    totalSpending = totalSpending,
                    averageOrderValue = averageOrderValue,
                    frequency = frequency,
                    preferredCategory = preferredCategory,
                    churned = churned
                )
            )
        }

    val errorFileName = "error_report.csv"
    File(errorFileName).writeText(errorList.toString())
    println("errors have been saved to $errorFileName")

    val url = args[1]
    println("importing to $url")
    //uploadToServer(url, customerList)
}

suspend fun uploadToServer(url:String, customerList: ArrayList<Customer>){
    val json = Json { prettyPrint = true }
    val client = HttpClient(OkHttp)

    val unableToUpload = ArrayList<Customer>()
    for (customer in customerList) {
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(customer).replace(": null\n", ": \"\"\n"))
        }
        if (response.status != HttpStatusCode.OK || response.status != HttpStatusCode.Created){
            unableToUpload.add(customer)
        }
    }
    if (unableToUpload.size > 0) {
        println("unable to upload these:\n${unableToUpload.joinToString ( "\n" )}")
    }
    client.close()
}

fun validateDates(date: String, errorList: ArrayList<String> = ArrayList()): String? {
    val slashPattern = Regex("^\\d{1,2}/\\d{1,2}/\\d{4}$")
    val isoWithTimePattern = Regex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
    val isoPattern = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    val validFormat: StringBuilder = StringBuilder()

    if (date.matches(slashPattern)) {
        val splitDate = date.split("/")
        val year = splitDate[2].toInt()

        validFormat.append("$year-")
        if (splitDate[1].toInt() > 12) {
            validFormat.append("${splitDate[0].toInt().toDateString()}-${splitDate[1].toInt().toDateString()}")
        } else {
            validFormat.append("${splitDate[1].toInt().toDateString()}-${splitDate[0].toInt().toDateString()}")
        }
    } else if (date.matches(isoWithTimePattern)) {
        validFormat.append(date.split(" ")[0])
    } else if (date.matches(isoPattern)) {
        validFormat.append(date)
    } else
        errorList.add("Invalid date format")

    return if (validFormat.toString().isNotEmpty())
        validFormat.toString()
    else
        null
}

fun Int.toDateString(): String {
    if (this < 10)
        return "0$this"
    return this.toString()
}

