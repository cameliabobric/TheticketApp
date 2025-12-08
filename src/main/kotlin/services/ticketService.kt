

package services

import data.*
import utils.ConsoleUI
import java.time.LocalDate
import kotlin.system.exitProcess

class TicketService(private val machine: TicketMachine) {

    private var currentBalance: Double = 0.0

    /**
     * Search for tickets by destination and type
     * Requirement: a) User should be able to search for a ticket by destination and ticket type
     */
    fun searchTickets() {
        ConsoleUI.showMessage("\n=== SEARCH TICKETS ===")

        // Display available destinations
        ConsoleUI.showMessage("\nAvailable Destinations:")
        machine.getDestinations().forEachIndexed { index, dest ->
            println("${index + 1}. ${dest.stationName}")
        }

        print("\nSelect destination (1-${machine.getDestinations().size}): ")
        val destChoice = readLine()?.toIntOrNull() ?: 0

        if (destChoice !in 1..machine.getDestinations().size) {
            ConsoleUI.showMessage("Invalid destination selection!")
            return
        }

        val selectedDestination = machine.getDestinations()[destChoice - 1]

        // Select ticket type
        ConsoleUI.showMessage("\nTicket Types:")
        println("1. Single - £${String.format("%.2f", selectedDestination.singlePrice)}")
        println("2. Return - £${String.format("%.2f", selectedDestination.returnPrice)}")

        print("\nSelect ticket type (1-2): ")
        val typeChoice = readLine()?.toIntOrNull() ?: 0

        if (typeChoice !in 1..2) {
            ConsoleUI.showMessage("Invalid ticket type selection!")
            return
        }

        val ticketType = if (typeChoice == 1) TicketType.SINGLE else TicketType.RETURN
        val price = if (ticketType == TicketType.SINGLE) {
            selectedDestination.singlePrice
        } else {
            selectedDestination.returnPrice
        }


        val activeOffer = machine.getActiveOfferForStation(selectedDestination.stationName)
        val finalPrice = if (activeOffer != null) {
            val discountedPrice = price * (1 - activeOffer.discount / 100)
            ConsoleUI.showMessage("\n🎉 SPECIAL OFFER APPLIED! ${activeOffer.discount}% OFF")
            ConsoleUI.showMessage("Original Price: £${String.format("%.2f", price)}")
            ConsoleUI.showMessage("Discounted Price: £${String.format("%.2f", discountedPrice)}")
            discountedPrice
        } else {
            price
        }

        ConsoleUI.showMessage("\n=== TICKET FOUND ===")
        println("From: ${machine.getOriginStation()}")
        println("To: ${selectedDestination.stationName}")
        println("Type: $ticketType")
        println("Price: £${String.format("%.2f", finalPrice)}")

        print("\nWould you like to purchase this ticket? (y/n): ")
        val purchase = readLine()?.toLowerCase() ?: "n"

        if (purchase == "y") {
            proceedToPurchase(selectedDestination, ticketType, finalPrice)
        }
    }

    /**
     * Insert money into the machine

     */
    fun insertMoney() {
        ConsoleUI.showMessage("\n=== INSERT MONEY ===")
        println("Current Balance: £${String.format("%.2f", currentBalance)}")

        print("Enter amount to insert (in pounds): £")
        val amount = readLine()?.toDoubleOrNull() ?: 0.0

        if (amount <= 0) {
            ConsoleUI.showMessage("Invalid amount! Please enter a positive value.")
            return
        }

        currentBalance += amount
        ConsoleUI.showMessage("✓ £${String.format("%.2f", amount)} inserted successfully!")
        ConsoleUI.showMessage("New Balance: £${String.format("%.2f", currentBalance)}")
    }

    /**
     * Purchase a ticket

     */
    private fun proceedToPurchase(destination: Destination, type: TicketType, price: Double) {
        ConsoleUI.showMessage("\n=== PURCHASE TICKET ===")

        // Check if enough money has been inserted
        while (currentBalance < price) {
            ConsoleUI.showMessage("\nInsufficient funds!")
            println("Required: £${String.format("%.2f", price)}")
            println("Current Balance: £${String.format("%.2f", currentBalance)}")

            print("\nInsert more money? (y/n): ")
            val choice = readLine()?.toLowerCase() ?: "n"

            if (choice != "y") {
                ConsoleUI.showMessage("Purchase cancelled.")
                return
            }

            insertMoney()
        }

        // Process the purchase
        currentBalance -= price
        destination.salesCount++
        machine.addToTakings(destination.stationName, price)

        // Create and display the ticket
        val ticket = Ticket(
            origin = machine.getOriginStation(),
            destination = destination.stationName,
            price = price,
            ticketType = type,
            purchaseDate = LocalDate.now()
        )

        displayTicket(ticket)

        // Return change if any
        if (currentBalance > 0) {
            ConsoleUI.showMessage("\nChange returned: £${String.format("%.2f", currentBalance)}")
            currentBalance = 0.0
        }

        ConsoleUI.showMessage("\nThank you for your purchase!")
    }

    /**
     * Display the ticket in the required format

     */
    private fun displayTicket(ticket: Ticket) {
        ConsoleUI.showMessage("\n" + "=".repeat(30))
        println("***")
        println(ticket.origin)
        println("to")
        println(ticket.destination)
        println("Price: ${String.format("%.2f", ticket.price)} [${ticket.ticketType}]")
        println("***")
        ConsoleUI.showMessage("=".repeat(30))
    }

    /**
     * Quick purchase option - combines search and purchase
     */
    fun quickPurchase(destinationName: String, type: TicketType) {
        val destination = machine.getDestinations().find {
            it.stationName.equals(destinationName, ignoreCase = true)
        }

        if (destination == null) {
            ConsoleUI.showMessage("Destination not found: $destinationName")
            return
        }

        val price = if (type == TicketType.SINGLE) {
            destination.singlePrice
        } else {
            destination.returnPrice
        }

        proceedToPurchase(destination, type, price)
    }

    /**
     * Get current balance
     */
    fun getBalance(): Double = currentBalance

    /**
     * Return money (cancel transaction)
     */
    fun returnMoney() {
        if (currentBalance > 0) {
            ConsoleUI.showMessage("\nReturning £${String.format("%.2f", currentBalance)}")
            currentBalance = 0.0
        } else {
            ConsoleUI.showMessage("No money to return.")
        }
    }
}

// Main function for Member A testing
fun main() {
    println("=== MEMBER A - TICKET SERVICE TESTING ===")
    println("Testing Vali's Implementation")

    // Initialize the ticket machine with test data
    val machine = TicketMachine()
    machine.initializeForMemberA()

    val ticketService = TicketService(machine)

    while (true) {
        println("\n=== TICKET MACHINE - Member A Functions ===")
        println("1. Search for tickets")
        println("2. Insert money")
        println("3. Check balance")
        println("4. Return money")
        println("5. Quick purchase test")
        println("0. Exit")

        print("\nSelect option: ")
        when (readLine()?.toIntOrNull() ?: -1) {
            1 -> ticketService.searchTickets()
            2 -> ticketService.insertMoney()
            3 -> println("Current Balance: £${String.format("%.2f", ticketService.getBalance())}")
            4 -> ticketService.returnMoney()
            5 -> {
                // Test quick purchase
                ticketService.insertMoney()
                ticketService.quickPurchase("Manchester", TicketType.SINGLE)
            }
            0 -> {
                println("Thank you for using the Ticket Machine!")
                exitProcess(0)
            }
            else -> println("Invalid option!")
        }
    }
}