package models

import java.time.LocalDate
import java.time.LocalDateTime

enum class TicketType{
    SINGLE , RETURN
}

data class Ticket(
    val origin :String,
    val ticketType : TicketType,
    val destination :String,
    val price : Double,
    val purchaseDate : LocalDate = LocalDate.now(),

    )
