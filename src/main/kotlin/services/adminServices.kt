

package services

import data.Destination
import utils.ConsoleUI
import kotlin.system.exitProcess

class AdminService(private val machine: TicketMachine) {

    /**
     * View all destinations with details
     * Requirement: d) Admin user should be able to view a list of all destinations
     */
    fun viewAllDestinations() {
        ConsoleUI.showMessage("\n=== ALL DESTINATIONS ===")
        ConsoleUI.showMessage("=" * 70)

        if (machine.getDestinations().isEmpty()) {
            ConsoleUI.showMessage("No destinations available.")
            return
        }

        // Header
        println(String.format("%-3s %-20s %-12s %-12s %-10s %-12s",
            "#", "Station Name", "Single (£)", "Return (£)", "Sales", "Revenue (£)"))
        println("-" * 70)

        // Display each destination
        machine.getDestinations().forEachIndexed { index, dest ->
            val revenue = machine.getStationTakings(dest.stationName)
            println(String.format("%-3d %-20s %-12.2f %-12.2f %-10d %-12.2f",
                index + 1,
                if (dest.stationName.length > 20) dest.stationName.take(17) + "..." else dest.stationName,
                dest.singlePrice,
                dest.returnPrice,
                dest.salesCount,
                revenue
            ))
        }

        // Summary statistics
        println("-" * 70)
        val totalSales = machine.getDestinations().sumOf { it.salesCount }
        val totalRevenue = machine.getTotalTakings()
        val avgSinglePrice = machine.getDestinations().map { it.singlePrice }.average()
        val avgReturnPrice = machine.getDestinations().map { it.returnPrice }.average()

        ConsoleUI.showMessage("\n=== SUMMARY ===")
        println("Total Destinations: ${machine.getDestinations().size}")
        println("Total Sales: $totalSales")
        println("Total Revenue: £${String.format("%.2f", totalRevenue)}")
        println("Average Single Price: £${String.format("%.2f", avgSinglePrice)}")
        println("Average Return Price: £${String.format("%.2f", avgReturnPrice)}")

        // Show most popular destination
        val mostPopular = machine.getDestinations().maxByOrNull { it.salesCount }
        if (mostPopular != null && mostPopular.salesCount > 0) {
            ConsoleUI.showMessage("\n📊 Most Popular Destination: ${mostPopular.stationName} (${mostPopular.salesCount} sales)")
        }
    }

    /**
     * Add a new destination
     * Requirement: e) Admin user should be able to add a destination
     */
    fun addDestination() {
        ConsoleUI.showMessage("\n=== ADD NEW DESTINATION ===")

        print("Enter station name: ")
        val stationName = readLine()?.trim() ?: ""

        if (stationName.isEmpty()) {
            ConsoleUI.showMessage("❌ Station name cannot be empty!")
            return
        }

        // Check if destination already exists
        if (machine.getDestinations().any { it.stationName.equals(stationName, ignoreCase = true) }) {
            ConsoleUI.showMessage("❌ Destination '$stationName' already exists!")
            return
        }

        print("Enter single ticket price (£): ")
        val singlePrice = readLine()?.toDoubleOrNull()

        if (singlePrice == null || singlePrice <= 0) {
            ConsoleUI.showMessage("❌ Invalid single ticket price!")
            return
        }

        print("Enter return ticket price (£): ")
        val returnPrice = readLine()?.toDoubleOrNull()

        if (returnPrice == null || returnPrice <= 0) {
            ConsoleUI.showMessage("❌ Invalid return ticket price!")
            return
        }

        // Validate return price is reasonable (usually less than 2x single)
        if (returnPrice < singlePrice) {
            ConsoleUI.showMessage("⚠️ Warning: Return price is less than single price!")
            print("Continue anyway? (y/n): ")
            if (readLine()?.toLowerCase() != "y") {
                ConsoleUI.showMessage("Operation cancelled.")
                return
            }
        }

        val newDestination = Destination(
            stationName = stationName,
            singlePrice = singlePrice,
            returnPrice = returnPrice,
            salesCount = 0
        )

        machine.addDestination(newDestination)
        ConsoleUI.showMessage(" Destination '$stationName' added successfully!")

        // Display the new destination details
        ConsoleUI.showMessage("\n=== NEW DESTINATION DETAILS ===")
        println("Station: $stationName")
        println("Single: £${String.format("%.2f", singlePrice)}")
        println("Return: £${String.format("%.2f", returnPrice)}")
    }


    fun updateDestination() {
        ConsoleUI.showMessage("\n=== UPDATE DESTINATION ===")

        if (machine.getDestinations().isEmpty()) {
            ConsoleUI.showMessage("No destinations available to update.")
            return
        }

        // Display destinations
        ConsoleUI.showMessage("\nAvailable Destinations:")
        machine.getDestinations().forEachIndexed { index, dest ->
            println("${index + 1}. ${dest.stationName} - Single: £${String.format("%.2f", dest.singlePrice)}, Return: £${String.format("%.2f", dest.returnPrice)}")
        }

        print("\nSelect destination to update (1-${machine.getDestinations().size}): ")
        val choice = readLine()?.toIntOrNull() ?: 0

        if (choice !in 1..machine.getDestinations().size) {
            ConsoleUI.showMessage("❌ Invalid selection!")
            return
        }

        val destination = machine.getDestinations()[choice - 1]

        ConsoleUI.showMessage("\n=== UPDATING: ${destination.stationName} ===")
        println("Current Single Price: £${String.format("%.2f", destination.singlePrice)}")
        println("Current Return Price: £${String.format("%.2f", destination.returnPrice)}")

        ConsoleUI.showMessage("\nWhat would you like to update?")
        println("1. Station name")
        println("2. Single ticket price")
        println("3. Return ticket price")
        println("4. Both prices")
        println("5. All details")
        println("0. Cancel")

        print("\nSelect option: ")
        when (readLine()?.toIntOrNull() ?: 0) {
            1 -> updateStationName(destination)
            2 -> updateSinglePrice(destination)
            3 -> updateReturnPrice(destination)
            4 -> {
                updateSinglePrice(destination)
                updateReturnPrice(destination)
            }
            5 -> {
                updateStationName(destination)
                updateSinglePrice(destination)
                updateReturnPrice(destination)
            }
            0 -> ConsoleUI.showMessage("Update cancelled.")
            else -> ConsoleUI.showMessage(" Invalid option!")
        }
    }

    private fun updateStationName(destination: Destination) {
        print("Enter new station name (current: ${destination.stationName}): ")
        val newName = readLine()?.trim() ?: ""

        if (newName.isNotEmpty() && newName != destination.stationName) {
            val oldName = destination.stationName
            destination.stationName = newName
            ConsoleUI.showMessage("Station name updated from '$oldName' to '$newName'")
        }
    }

    private fun updateSinglePrice(destination: Destination) {
        print("Enter new single ticket price (current: £${String.format("%.2f", destination.singlePrice)}): £")
        val newPrice = readLine()?.toDoubleOrNull()

        if (newPrice != null && newPrice > 0) {
            val oldPrice = destination.singlePrice
            destination.singlePrice = newPrice
            ConsoleUI.showMessage(" Single price updated from £${String.format("%.2f", oldPrice)} to £${String.format("%.2f", newPrice)}")
        } else if (newPrice != null) {
            ConsoleUI.showMessage(" Invalid price! Must be positive.")
        }
    }

    private fun updateReturnPrice(destination: Destination) {
        print("Enter new return ticket price (current: £${String.format("%.2f", destination.returnPrice)}): £")
        val newPrice = readLine()?.toDoubleOrNull()

        if (newPrice != null && newPrice > 0) {
            val oldPrice = destination.returnPrice
            destination.returnPrice = newPrice
            ConsoleUI.showMessage(" Return price updated from £${String.format("%.2f", oldPrice)} to £${String.format("%.2f", newPrice)}")
        } else if (newPrice != null) {
            ConsoleUI.showMessage(" Invalid price! Must be positive.")
        }
    }


    fun adjustAllPrices() {
        ConsoleUI.showMessage("\n=== ADJUST ALL PRICES ===")
        ConsoleUI.showMessage("This will adjust ALL ticket prices by a percentage.")

        if (machine.getDestinations().isEmpty()) {
            ConsoleUI.showMessage("No destinations available.")
            return
        }

        ConsoleUI.showMessage("\nQuick options:")
        println("1. Increase by 5%")
        println("2. Increase by 10%")
        println("3. Decrease by 5%")
        println("4. Decrease by 10%")
        println("5. Custom percentage")
        println("0. Cancel")

        print("\nSelect option: ")
        val factor = when (readLine()?.toIntOrNull() ?: 0) {
            1 -> 1.05
            2 -> 1.10
            3 -> 0.95
            4 -> 0.90
            5 -> {
                print("Enter percentage change (e.g., 15 for +15%, -20 for -20%): ")
                val percent = readLine()?.toDoubleOrNull() ?: 0.0
                1 + (percent / 100)
            }
            0 -> {
                ConsoleUI.showMessage("Operation cancelled.")
                return
            }
            else -> {
                ConsoleUI.showMessage(" Invalid option!")
                return
            }
        }

        // Show preview of changes
        ConsoleUI.showMessage("\n=== PRICE CHANGE PREVIEW ===")
        val percentChange = ((factor - 1) * 100)
        val changeType = if (percentChange > 0) "increase" else "decrease"
        ConsoleUI.showMessage("${Math.abs(percentChange)}% $changeType will be applied")

        println("\nSample changes:")
        machine.getDestinations().take(3).forEach { dest ->
            val newSingle = dest.singlePrice * factor
            val newReturn = dest.returnPrice * factor
            println("${dest.stationName}:")
            println("  Single: £${String.format("%.2f", dest.singlePrice)} → £${String.format("%.2f", newSingle)}")
            println("  Return: £${String.format("%.2f", dest.returnPrice)} → £${String.format("%.2f", newReturn)}")
        }

        print("\nConfirm price adjustment for ALL destinations? (y/n): ")
        if (readLine()?.toLowerCase() != "y") {
            ConsoleUI.showMessage("Operation cancelled.")
            return
        }

        // Apply the price adjustment
        var updated = 0
        machine.getDestinations().forEach { dest ->
            dest.singlePrice *= factor
            dest.returnPrice *= factor
            updated++
        }

        ConsoleUI.showMessage("\n Successfully updated prices for $updated destinations!")
        ConsoleUI.showMessage("All prices have been ${changeType}d by ${Math.abs(percentChange)}%")
    }

    /**
     * Generate a revenue report
     */
    fun generateRevenueReport() {
        ConsoleUI.showMessage("\n=== REVENUE REPORT ===")
        val totalRevenue = machine.getTotalTakings()
        val destinations = machine.getDestinations().sortedByDescending {
            machine.getStationTakings(it.stationName)
        }

        ConsoleUI.showMessage("\nTop Performing Destinations:")
        destinations.take(5).forEach { dest ->
            val revenue = machine.getStationTakings(dest.stationName)
            val percentage = if (totalRevenue > 0) (revenue / totalRevenue) * 100 else 0.0
            println("${dest.stationName}: £${String.format("%.2f", revenue)} (${String.format("%.1f", percentage)}% of total)")
        }

        ConsoleUI.showMessage("\nTotal System Revenue: £${String.format("%.2f", totalRevenue)}")
    }
}

// Main function for Member B testing
fun main() {
    println("=== MEMBER B - ADMIN SERVICE TESTING ===")
    println("Testing Camelia's Implementation")

    // Initialize the ticket machine with test data
    val machine = TicketMachine()
    machine.initializeForMemberB()

    val adminService = AdminService(machine)

    // Note: Member B doesn't implement login, so we proceed directly to admin functions
    while (true) {
        println("\n=== ADMIN DASHBOARD - Member B Functions ===")
        println("1. View all destinations")
        println("2. Add new destination")
        println("3. Update destination")
        println("4. Adjust all prices")
        println("5. Generate revenue report")
        println("0. Exit")

        print("\nSelect option: ")
        when (readLine()?.toIntOrNull() ?: -1) {
            1 -> adminService.viewAllDestinations()
            2 -> adminService.addDestination()
            3 -> adminService.updateDestination()
            4 -> adminService.adjustAllPrices()
            5 -> adminService.generateRevenueReport()
            0 -> {
                println("Exiting admin dashboard...")
                exitProcess(0)
            }
            else -> println("Invalid option!")
        }
    }
}