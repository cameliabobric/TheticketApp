package com.ticketmachine.data

import java.time.LocalDate

enum class TicketType {
    SINGLE, RETURN
}

data class Ticket(
    val origin: String,
    val destination: String,
    val price: Double,
    val ticketType: TicketType,
    val purchaseDate: LocalDate = LocalDate.now()
)

data class Destination(
    var stationName: String,
    var singlePrice: Double,
    var returnPrice: Double,
    var salesCount: Int = 0
)

data class User(
    val username: String,
    val password: String,
    val isAdmin: Boolean
)

data class SpecialOffer(
    val id: Int,
    val stationName: String,
    val discount: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String
)
