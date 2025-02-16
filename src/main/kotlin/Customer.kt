package hu.notkulonme

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
