package io.github.binishmanandhar23.verticalcalendarlibrary.model

class CalendarDateModel(yearParam: Int, monthParam: Int, dateParam: Int) {
    var dateInString: String? = dateParam.toString()
    var monthInString: String? = monthParam.toString()
    var yearInString: String? = yearParam.toString()
    var date: Int = dateParam
    var month: Int = monthParam
    var year: Int = yearParam
}