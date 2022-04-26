package io.github.binishmanandhar23.verticalcalendarlibrary.model

data class PopulatingData(var medicationDay: Collection<CalendarDay>? = null,
                          var appointmentDay: Collection<CalendarDay>?= null,
                          var popularDay: Collection<CalendarDay>?= null,
                          var mindfulDay: Collection<CalendarDay>?= null,
                          var highlighted: Collection<CalendarDay>? = null,
                          )