package models
import java.time.LocalDate

data class SpecialOffer(
    val id: Int ,
    val stationName : String,
    val discount : Double,
    val startDate : LocalDate ,
    val endDate : LocalDate ,
    val desccription : String

)