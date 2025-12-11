package services

import data.Destination
import utils.ConsoleUI


class AdminService(private val machine: TicketMachine) {


    fun viewAllDestinations() {
        // TODO: Display header
        println("\n*** ALL DESTINATIONS ***")
        println("*" * 40)

        // TODO: Check if destinations exist
        val destinations = machine.getDestinations()
        if (destinations.isEmpty()) {
            println("No destinations available.")
            return
        }

        // display table header
        println(String.format("%-3s %-20s %-12s %-12s %-10s",
            "#", "Station Name", "Single (£)", "Return (£)", "Sales"))
        println("-" * 60)

        // display each destination
        destinations.forEachIndexed { index, dest ->
            // IMPLEMENT: Format and display destination info
            println(String.format("%-3d %-20s %-12.2f %-12.2f %-10d",
                index + 1,
                dest.stationName,
                dest.singlePrice,
                dest.returnPrice,
                dest.salesCount
            ))
        }

        // display summary statistics
        println("-" * 40)
        val totalSales = destinations.sumOf { it.salesCount }
        val avgSinglePrice = destinations.map { it.singlePrice }.average()
        val avgReturnPrice = destinations.map { it.returnPrice }.average()

        println("\n*** SUMMARY ***")
        println("Total Destinations: ${destinations.size}")
        println("Total Sales: $totalSales")
        println("Average Single Price: £${String.format("%.2f", avgSinglePrice)}")
        println("Average Return Price: £${String.format("%.2f", avgReturnPrice)}")
    }