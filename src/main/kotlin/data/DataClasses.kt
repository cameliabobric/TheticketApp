package data

import java.time.LocalDate

// TICKET TYPE
enum class TicketType {
    SINGLE, RETURN
}

// TICKET
data class Ticket(
    val origin: String,
    val destination: String,
    val price: Double,
    val ticketType: TicketType,
    val purchaseDate: LocalDate = LocalDate.now()
)

// DESTINATION
data class Destination(
    var stationName: String,
    var singlePrice: Double,
    var returnPrice: Double,
    var salesCount: Int = 0
)

// USER (for Member C)
data class User(
    val username: String,
    val password: String,
    val isAdmin: Boolean
)

// SPECIAL OFFER (for Member C)
data class SpecialOffer(
    val id: Int,
    val stationName: String,
    val discount: Double,   // 20.0 = 20%
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String
)
