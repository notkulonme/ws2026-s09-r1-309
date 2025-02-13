package org.example

import kotlinx.serialization.Serializable

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
            return "$line,\"${errors.joinToString(", ")}\"\n"
        else if (errors.size == 1)
            return "$line,${errors[0]}\n"
        else
            return ""
    }
}

@Serializable
data class CustomerList(
    val customers: ArrayList<Customer> = ArrayList()
){
    fun add(customer: Customer){
        customers.add(customer)
    }
}
