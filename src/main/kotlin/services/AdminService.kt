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

    fun addDestination() {

        println("\n=== ADD NEW DESTINATION ===")

        //get station name
        print("Enter station name: ")
        val stationName = readLine()?.trim() ?: ""

        // validate  station name not empty
        if (stationName.isEmpty()) {
            println("❌ Station name cannot be empty!")
            return
        }

        // check if destination already exists
        if (machine.getDestinations().any { it.stationName.equals(stationName, ignoreCase = true) }) {
            println("❌ Destination '$stationName' already exists!")
            return
        }

        // get single ticket price
        print("Enter single ticket price (£): ")
        val singlePrice = readLine()?.toDoubleOrNull()

        // validate single price
        if (singlePrice == null || singlePrice <= 0) {
            println("❌ Invalid single ticket price!")
            return
        }

        // get return ticket price
        print("Enter return ticket price (£): ")
        val returnPrice = readLine()?.toDoubleOrNull()

        // validate return price
        if (returnPrice == null || returnPrice <= 0) {
            println("❌ Invalid return ticket price!")
            return
        }

        // create new destination
        val newDestination = Destination(
            stationName = stationName,
            singlePrice = singlePrice,
            returnPrice = returnPrice,
            salesCount = 0
        )

        // add to machine
        machine.addDestination(newDestination)

        // confirm addition
        println("✅ Destination '$stationName' added successfully!")
        println("Single: £${String.format("%.2f", singlePrice)}")
        println("Return: £${String.format("%.2f", returnPrice)}")
    }

    fun updateDestination() {
        // display header
        println("\n*** UPDATE DESTINATION ***")

        // check if destinations exist
        val destinations = machine.getDestinations()
        if (destinations.isEmpty()) {
            println("No destinations to update.")
            return
        }

        // display destinations to choose from
        println("Select destination to update:")
        destinations.forEachIndexed { index, dest ->
            println("${index + 1}. ${dest.stationName} - Single: £${dest.singlePrice}, Return: £${dest.returnPrice}")
        }

        // get selection
        print("Select (1-${destinations.size}): ")
        val choice = readLine()?.toIntOrNull() ?: 0

        // validate selection
        if (choice !in 1..destinations.size) {
            println(" Invalid selection!")
            return
        }

        val destination = destinations[choice - 1]

        //  update menu
        println("\n=== UPDATING: ${destination.stationName} ===")
        println("Current Single Price: £${destination.singlePrice}")
        println("Current Return Price: £${destination.returnPrice}")

        println("\nWhat to update?")
        println("1. Station name")
        println("2. Single ticket price")
        println("3. Return ticket price")
        println("4. Both prices")
        println("0. Cancel")

        //  update choice
        print("Choice: ")
        when (readLine()?.toIntOrNull()) {
            1 -> {
                // IMPLEMENT: Update station name
                print("New station name (current: ${destination.stationName}): ")
                val newName = readLine()?.trim()
                if (!newName.isNullOrEmpty()) {
                    destination.stationName = newName
                    println("Station name updated to '$newName'")
                }
            }
            2 -> {
                // IMPLEMENT: Update single price
                print("New single price (current: £${destination.singlePrice}): £")
                val newPrice = readLine()?.toDoubleOrNull()
                if (newPrice != null && newPrice > 0) {
                    destination.singlePrice = newPrice
                    println(" Single price updated to £$newPrice")
                }
            }
            3 -> {
                // IMPLEMENT: Update return price
                print("New return price (current: £${destination.returnPrice}): £")
                val newPrice = readLine()?.toDoubleOrNull()
                if (newPrice != null && newPrice > 0) {
                    destination.returnPrice = newPrice
                    println(" Return price updated to £$newPrice")
                }
            }
            4 -> {
                // IMPLEMENT: Update both prices
                print("New single price: £")
                val newSingle = readLine()?.toDoubleOrNull()
                if (newSingle != null && newSingle > 0) {
                    destination.singlePrice = newSingle
                }

                print("New return price: £")
                val newReturn = readLine()?.toDoubleOrNull()
                if (newReturn != null && newReturn > 0) {
                    destination.returnPrice = newReturn
                }
                println("Prices updated!")
            }
            0 -> println("Update cancelled.")
            else -> println("Invalid option!")
        }
    }