package com.ticketmachine

import data.Destination
import data.SpecialOffer
import java.time.LocalDate


class TicketMachine {


    private val destinations = mutableListOf<Destination>()


    private val stationTakings = mutableMapOf<String, Double>()


    private val originStation = "Birmingham Central"


    private val specialOffers = mutableListOf<SpecialOffer>()

    init {

        initializeDestinations()
    }


    private fun initializeDestinations() {
        val defaultDestinations = listOf(
            Destination("London", 45.50, 75.00, 0),
            Destination("Manchester", 25.00, 40.00, 0),
            Destination("Liverpool", 30.00, 50.00, 0),
            Destination("Edinburgh", 65.00, 110.00, 0),
            Destination("Cardiff", 35.00, 60.00, 0),
            Destination("Bristol", 28.00, 48.00, 0),
            Destination("Newcastle", 55.00, 95.00, 0),
            Destination("Glasgow", 70.00, 120.00, 0),
            Destination("Leeds", 32.00, 54.00, 0),
            Destination("Sheffield", 22.00, 38.00, 0)
        )

        destinations.addAll(defaultDestinations)


        destinations.forEach { dest ->
            stationTakings[dest.stationName] = 0.0
        }
    }


    fun getDestinations(): MutableList<Destination> = destinations


    fun addDestination(destination: Destination) {
        destinations.add(destination)
        stationTakings[destination.stationName] = 0.0
    }


    fun getOriginStation(): String = originStation

    fun addToTakings(stationName: String, amount: Double) {
        val current = stationTakings[stationName] ?: 0.0
        stationTakings[stationName] = current + amount
    }

    // get takings for a specific station
    fun getStationTakings(stationName: String): Double {
        return stationTakings[stationName] ?: 0.0
    }

    // get total takings
    fun getTotalTakings(): Double {
        return stationTakings.values.sum()
    }

    // get active special offer for station
    fun getActiveOfferForStation(stationName: String): SpecialOffer? {
        val today = LocalDate.now()
        return specialOffers
            .filter { offer ->
                offer.stationName.equals(stationName, ignoreCase = true) &&
                        !today.isBefore(offer.startDate) &&
                        !today.isAfter(offer.endDate)
            }
            .maxByOrNull { it.discount }  // Highest discount if multiple
    }


    fun addSpecialOffer(offer: SpecialOffer) {
        specialOffers.add(offer)
    }


    fun getSpecialOffers(): List<SpecialOffer> = specialOffers.toList()

    // Optional: extra data for testing
    fun initializeForTesting() {
        destinations.find { it.stationName == "London" }?.salesCount = 5
        stationTakings["London"] = 227.50
    }
}
