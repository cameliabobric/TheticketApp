package services

import com.ticketmachine.TicketMachine
import data.*
import java.time.LocalDate

/**
 * MEMBER A (VALI)
 * Responsibilities:
 * - Ticket search by destination and type
 * - Money insertion and balance management
 * - Ticket purchase with validation
 * - Station takings tracking
 * - (Optional) Apply special offers from TicketMachine (Member C)
 */
class TicketService(private val machine: TicketMachine) {

    // Tracks money inserted by user
    private var currentBalance: Double = 0.0

    /**
     * Search tickets by destination and ticket type
     */
    fun searchTickets() {
        println("\n=== SEARCH TICKETS ===")

        val destinations = machine.getDestinations()
        if (destinations.isEmpty()) {
            println("No destinations available.")
            return
        }

        println("Available destinations:")
        destinations.forEachIndexed { index, dest ->
            println(
                "${index + 1}. ${dest.stationName} " +
                        "(Single £${formatPrice(dest.singlePrice)}, Return £${formatPrice(dest.returnPrice)})"
            )
        }

        print("Select destination (1-${destinations.size}): ")
        val destChoice = readLine()?.toIntOrNull()
        if (destChoice == null || destChoice !in 1..destinations.size) {
            println("Invalid destination selection!")
            return
        }

        val selectedDest = destinations[destChoice - 1]

        println("\nTicket types:")
        println("1. Single - £${formatPrice(selectedDest.singlePrice)}")
        println("2. Return - £${formatPrice(selectedDest.returnPrice)}")
        print("Select type (1-2): ")

        val typeChoice = readLine()?.toIntOrNull()
        if (typeChoice == null || typeChoice !in 1..2) {
            println("Invalid ticket type!")
            return
        }

        val ticketType = if (typeChoice == 1) TicketType.SINGLE else TicketType.RETURN
        val basePrice = if (ticketType == TicketType.SINGLE) {
            selectedDest.singlePrice
        } else {
            selectedDest.returnPrice
        }

        // OPTIONAL: apply special offer if exists (Member C)
        val offer = machine.getActiveOfferForStation(selectedDest.stationName)
        val finalPrice = if (offer != null) {
            val discountAmount = basePrice * (offer.discount / 100.0)
            val discounted = basePrice - discountAmount
            println("\nSpecial offer for ${selectedDest.stationName}: ${offer.discount}% off")
            println("Offer: ${offer.description}")
            println("Original price: £${formatPrice(basePrice)}")
            println("Discounted price: £${formatPrice(discounted)}")
            discounted
        } else {
            basePrice
        }

        println("\n=== TICKET FOUND ===")
        println("From: ${machine.getOriginStation()}")
        println("To: ${selectedDest.stationName}")
        println("Type: ${prettyType(ticketType)}")
        println("Price to pay: £${formatPrice(finalPrice)}")

        print("\nPurchase this ticket? (y/n): ")
        if (readLine()?.lowercase() == "y") {
            proceedToPurchase(selectedDest, ticketType, finalPrice)
        } else {
            println("Purchase cancelled.")
        }
    }

    /**
     * Insert money into the machine
     */
    fun insertMoney() {
        println("\n=== INSERT MONEY ===")
        println("Current balance: £${formatPrice(currentBalance)}")

        print("Enter amount to insert: £")
        val amount = readLine()?.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            println("Invalid amount! Must be a positive number.")
            return
        }

        currentBalance += amount
        println("✓ £${formatPrice(amount)} inserted successfully!")
        println("New balance: £${formatPrice(currentBalance)}")
    }

    /**
     * Complete purchase with validation and change
     */
    private fun proceedToPurchase(
        destination: Destination,
        type: TicketType,
        price: Double
    ) {
        println("\n=== PURCHASE TICKET ===")

        while (currentBalance < price) {
            println("Insufficient funds!")
            println("Required: £${formatPrice(price)}")
            println("Current balance: £${formatPrice(currentBalance)}")

            print("Insert more money? (y/n): ")
            if (readLine()?.lowercase() != "y") {
                println("Purchase cancelled.")
                return
            }

            insertMoney()
        }

        // Deduct price
        currentBalance -= price

        // Update destination sales count
        destination.salesCount++

        // Track takings in TicketMachine
        machine.addToTakings(destination.stationName, price)

        // Create ticket (purchaseDate default = today)
        val ticket = Ticket(
            origin = machine.getOriginStation(),
            destination = destination.stationName,
            price = price,
            ticketType = type,
            purchaseDate = LocalDate.now()
        )

        displayTicket(ticket)

        if (currentBalance > 0) {
            println("\nChange returned: £${formatPrice(currentBalance)}")
            currentBalance = 0.0
        }

        println("\nThank you for your purchase!")
    }

    /**
     * Display ticket in required format:
     * ***
     * [ORIGIN]
     * to
     * [DESTINATION]
     * Price: X.XX [Single or Return]
     * ***
     */
    private fun displayTicket(ticket: Ticket) {
        println("\n***")
        println(ticket.origin)
        println("to")
        println(ticket.destination)
        println("Price: ${formatPrice(ticket.price)} [${prettyType(ticket.ticketType)}]")
        println("***")
    }

    private fun formatPrice(value: Double): String =
        String.format("%.2f", value)

    private fun prettyType(type: TicketType): String =
        when (type) {
            TicketType.SINGLE -> "Single"
            TicketType.RETURN -> "Return"
        }

    fun getBalance(): Double = currentBalance

    fun returnMoney() {
        if (currentBalance > 0) {
            println("Returning £${formatPrice(currentBalance)}")
            currentBalance = 0.0
        } else {
            println("No money to return.")
        }
    }
}

// ===== SIMPLE MAIN FOR TESTING MEMBER A =====
fun main() {
    println("=== MEMBER A  - TESTING ===")

    val machine = TicketMachine()
    val service = TicketService(machine)

    while (true) {
        println("\n--- MEMBER A TEST MENU ---")
        println("1. Search tickets")
        println("2. Insert money")
        println("3. Check balance")
        println("4. Return money")
        println("0. Exit")

        print("Choice: ")
        when (readLine()?.toIntOrNull()) {
            1 -> service.searchTickets()
            2 -> service.insertMoney()
            3 -> println("Balance: £${service.getBalance()}")
            4 -> service.returnMoney()
            0 -> {
                println("Goodbye!")
                return
            }
            else -> println("Invalid option!")
        }
    }
}
// ===== SIMPLE MAIN FOR TESTING MEMBER A =====
fun main() {
    println("=== MEMBER A (VALI) - TESTING ===")

    val machine = TicketMachine()
    val service = TicketService(machine)

    while (true) {
        println("\n--- MEMBER A TEST MENU ---")
        println("1. Search tickets")
        println("2. Insert money")
        println("3. Check balance")
        println("4. Return money")
        println("0. Exit")

        print("Choice: ")
        when (readLine()?.toIntOrNull()) {
            1 -> service.searchTickets()
            2 -> service.insertMoney()
            3 -> println("Balance: £${service.getBalance()}")
            4 -> service.returnMoney()
            0 -> {
                println("Goodbye!")
                return
            }
            else -> println("Invalid option!")
        }
    }
}
