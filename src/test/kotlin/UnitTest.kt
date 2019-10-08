package org.spekframework.spek2
import Babysitter
import BabysitterInputService
import BabysitterService
import BabysitterWorkDay
import org.spekframework.spek2.style.gherkin.*
import java.time.LocalTime
import kotlin.properties.Delegates
import kotlin.test.assertEquals


object BabysitterServiceFeature: Spek({
    Feature("Paying babysitter for one night") {
        //        val set by memoized { mutableSetOf<String>() }
        val babysitterInputService = BabysitterInputService()

        Scenario("Working unsupported hours") {

            var validInput by Delegates.notNull<Boolean>()

            When("A babysitter enters their start time before 5PM") {
                validInput = babysitterInputService.getHourInputForTest(16, 20, 2)
            }
            Then("it should throw invalid time error") {
                assertEquals(false, validInput)
            }

            When("A babysitter enters their end time after 4AM") {
                validInput = babysitterInputService.getHourInputForTest(17, 20, 5)
            }
            Then("it should throw invalid time error") {
                assertEquals(false, validInput)
            }

            When("A babysitter enters the bed time before their start time") {
                validInput = babysitterInputService.getHourInputForTest(20, 19, 2)
            }
            Then("it should throw invalid time error") {
                assertEquals(false, validInput)
            }

            When("A babysitter enters an end time before midnight") {
                validInput = babysitterInputService.getHourInputForTest(17, 19, 22)
            }
            Then("it should throw invalid time error") {
                assertEquals(false, validInput)
            }
        }
        Scenario("Working supported hours") {

            var validInput by Delegates.notNull<Boolean>()
            val workDay = BabysitterWorkDay(
                LocalTime.of(0,0,0),
                LocalTime.of(3,0,0),
                LocalTime.of(9,0,0))
            val babysitter = Babysitter(workDay)

            When("A babysitter enters a valid time") {
                validInput = babysitterInputService.getHourInputForTest(17, 20, 2)
            }
            Then("it should be valid input") {
                assertEquals(true, validInput)
            }
            Then("it should create a valid work day") {
                assertEquals(workDay, babysitterInputService.getBabysitterWorkday())
            }
            Then("it should calculate the correct pay") {
                assertEquals(100.00, BabysitterService(babysitter).BabysitterCalcPay())
            }
        }
        //TODO: Implement more test cases
    }
})
