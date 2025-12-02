package models

data class Destination(
    var name: String,           // Station name
    var singlePrice: Double,     // Price for single ticket
    var returnPrice: Double,     // Price for return ticket
    var ticketsSold: Int = 0,    // Number of tickets sold to this destination
    var takings: Double = 0.0    // Total revenue from this destination
)
}

fun recordSale(price: Double) {
    // add the record of the sales
    ticketsSold++
    takings += price
    println("Sale recorded for $name: Ticket #$ticketsSold, Total takings: £%.2f".format(takings))
}