package ticketmachine


class TicketMachine{
    private val destination = mutableListOf<Destination>()
    private val users = mutableListOf<User>()
    private val specialOffers = mutableListOf<SpecialOffers>()
    private val stationTakings = mutableMapOf<String, Double>()
}