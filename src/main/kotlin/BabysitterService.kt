import java.time.LocalTime
import java.time.temporal.ChronoUnit

/**
 * Data class implementing the rate a babysitter charges
 *
 * Testing here
 *
 * @property start The starting time for the rate in babysitter time
 * @property end The ending time for the rate in babysitter time
 * @property rate The rate charged for the time slot
 */
data class BabysitterTimeRate(val start: LocalTime,
                              val end: LocalTime,
                              val rate: Double)


/**
 * Data class implementing representing the work day
 *
 * The 24-hour babysitter workday starts at 5PM and ends at 4AM
 *
 * @property start The starting time for the day
 * @property bed The bed time for the day
 * @property end The ending time for the day
 */
data class BabysitterWorkDay(val start: LocalTime,
                             val bed: LocalTime,
                             val end: LocalTime)

/**
 * The babysitter
 *
 * Defines the work hours and rates of the babysitter
 *
 * @property workDay The starting time for the day
 */
class Babysitter(val workDay: BabysitterWorkDay) {

    /**
     * Defines the rates for a workday
     * @return The contiguous work hour ranges and their rates
     */
    fun getTimeRateList(): List<BabysitterTimeRate> {
        /*TODO: This could be externalized
                The list should specify a contiguous set of non-overlapping times in
                BabySitterWorkDay time
        */
        return listOf(
            BabysitterTimeRate(
                LocalTime.of(workDay.start.hour, 0, 0),
                LocalTime.of(workDay.bed.hour, 0, 0),
                12.0),
            BabysitterTimeRate(
                LocalTime.of(workDay.bed.hour, 0, 0),
                LocalTime.of(7,0,0),
                8.0),
            BabysitterTimeRate(
                LocalTime.of(7, 0, 0),
                LocalTime.of(workDay.end.hour,0,0),
                16.0)
        )
    }
}
/**
 * This class handles converting input into a babysitter work day format
 *
 * Handles getting input, validating input,
 * and transforming into a babysitter workday
 *
 */
/*
    Handles getting input, validating input,
    and transforming into a babysitter workday
 */
//TODO: INJECT VALID TIMES BUSINESS RULES
class BabysitterInputService {

    //expose these times
    var startTime = LocalTime.of(0, 0, 0)
    var bedTime = LocalTime.of(0,0,0)
    var endTime = LocalTime.of(0,0,0)

    /**
     * Get start, bed, and end hours in military time from console
     * valding the input and converting to a babysitter work day format
     */
    fun getHourInputFromConsole(): Boolean {


        print("Please enter the military hour you started (17-23): ")
        val startHour = readLine()?.toIntOrNull()
        print("Please enter the military hour for bedtime (18-24): ")
        val bedHour = readLine()?.toIntOrNull()
        print("Please enter the military hour you ended (00-4): ")
        val endHour = readLine()?.toIntOrNull()

        //basic logging
        println("start time hour: $startHour")
        println("bed time hour: $bedHour")
        println("end time hour: $endHour")

        val isGood = validateHourInput(startHour, bedHour, endHour)
        if(!isGood)
            return false
        return true
    }

    /**
     * Get start, bed, and end hours in military time for test cases
     */
    fun getHourInputForTest(startHour: Int, bedHour: Int, endHour: Int): Boolean {

        return validateHourInput(startHour, bedHour, endHour)
    }

    /**
     * Validates business rules regarding start, bed, and end times
     */
    private fun validateHourInput(startHour: Int?,
                                  bedHour: Int?,
                                  endHour: Int?): Boolean {

        val errMsg = "Invalid input. All hours must be numeric " +
                "and bed time must be after start time.\n" +
                "Start Time (17-23) = $startHour \n" +
                "Bed Time (18-24) = $bedHour \n" +
                "End Time (00-04) = $endHour \n"

        //basic validation and logging
        if(startHour == null) {
            println(errMsg)
            return false
        }
        if(bedHour == null) {
            println(errMsg)
            return false
        }
        if(endHour == null) {
            println(errMsg)
            return false
        }

        //TODO: Remove hardcoding
        if(startHour !in 17..23) {
            println(errMsg)
            return false
        }

        if(bedHour !in 18..24 || bedHour < startHour) {
            println(errMsg)
            return false
        }
        if(endHour !in 0..4) {
            println(errMsg)
            return false
        }

        transformHourTime(startHour, bedHour, endHour)

        return true
    }

    /**
     * Converts a 24 hour day to a babysitter work day.
     * The babysitter work day starts at 5PM and ends at 4AM
     */
    private fun transformHourTime(startHour: Int,
                          bedHour: Int,
                          endHour: Int) {
        /*
            5AM-4PM = 5 - 16 -> invalid
            start of day = 5PM = 17 -> 0
            6PM = 18 -> 1
            7PM = 19 -> 2
            8PM = 20 -> 3
            9PM = 21 -> 4
            10PM = 22 -> 5
            11PM = 23 -> 6
            Midnight 24/0 -> 7
            1AM = 1 -> 8
            2AM = 2 -> 9
            3AM = 3 -> 10
            end of day = 4AM = 4 -> 11
         */
        //TODO: Remove hardcoding
        val tmpStartHour = if(startHour >= 17) startHour-17 else startHour+7
        val tmpBedHour = if(bedHour >= 17) bedHour-17 else bedHour+7
        val tmpEndHour = if(endHour >= 17) endHour-17 else endHour+7

        //logging
        println("Converted start hour: $tmpStartHour")
        println("Converted bed hour: $tmpBedHour")
        println("Converted end hour: $tmpEndHour")

        startTime = LocalTime.of(tmpStartHour,0 , 0)
        bedTime = LocalTime.of(tmpBedHour, 0, 0)
        endTime = LocalTime.of(tmpEndHour, 0, 0)

        //logging
        println("Converted start time: $startTime")
        println("Converted bed time: $bedTime")
        println("Converted end time: $endTime")
    }

    /**
     * Returns the time in babysitter work day format
     */
    fun getBabysitterWorkday(): BabysitterWorkDay {

        return BabysitterWorkDay(startTime, bedTime, endTime)
    }
}

/*
This is the service which enables a babysitter to calculate
their nightly pay
 */
class BabysitterService(val babysitter: Babysitter) {

    fun BabysitterCalcPay(): Double {

        var value: Double = 0.0
        for (timeSlot in babysitter.getTimeRateList())
            value += ChronoUnit.HOURS.between(timeSlot.start, timeSlot.end) * timeSlot.rate

        println(value)
        return value
    }
}