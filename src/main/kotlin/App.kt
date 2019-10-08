import kotlin.system.exitProcess


/*
A simple driver program for the babysitter service

Business Rules:
Start time must be between 5PM and 11PM
Bed time must be between 6PM and midnight and after start time
End time must be between midnight and 4AM next day

 */

fun main(arg: Array<String>) {

    //Input
    val timeInput = BabysitterInputService()
    val isInputGood = timeInput.getHourInputFromConsole()
    if(!isInputGood)
        exitProcess(-1)
    val workday = timeInput.getBabysitterWorkday()

    //Process
    val babysitter = Babysitter(workday)
    val babysitterService = BabysitterService(babysitter)
    val income = babysitterService.BabysitterCalcPay()

    //Output
    println("INCOME = $income ")
}