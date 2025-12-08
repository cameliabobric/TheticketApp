package models

class TicketService(private val machine : TicketMachine){
    private var currentBalance : Double = 0.0

    //search for tickets by destination

    fun searchTickets(){
        println(" Search tickets ")

        //display available destinations

        println("\nAvailable destinations: ")
        machine.getdestinations().forEachIndexed { index, dest ->
            println("${index + 1 }.${dest.stationName}")
        }

        // select destinations return false if destination is null or not a string

        println("\nSelect destination (1-${machine.getDestinations().size}): ")
        val destChoice = readLine()?.toIntOrNull() ?: 0

        if (destChoice !in 1..machine.getDestination().size) {
            println("Invalid input.")
            return
        }


    }
}