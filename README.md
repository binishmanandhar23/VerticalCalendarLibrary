# VerticalCalendarLibrary
Vertical Calendar library originally made for **Bonzun IVF** using **Jetpack Compose**

![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)

## Installation

Add the following dependencies in the gradle file of your app module to get started:  

Gradle
# In project level build.gradle or in setting.gradle file
```kotlin
repositories {
    maven{
        url "https://jitpack.io"
    }
}
```
# In app level build.gradle
```kotlin
dependencies {
    implementation 'com.github.binishmanandhar23:verticalcalendarlibrary:1.0.0'
}
```
Maven
```xml
<dependency>
  <groupId>com.github.binishmanandhar23:verticalcalendarlibrary:1.0.0</groupId>
  <artifactId>verticalcalendarlibrary</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

or if you want to further customize the module, simply import it.

## Initial Setup
I'm using org.threeten:threetenbp library for working with dates which is why the library must be initialized in the Application file or your MainActivity's **onCreate()** before accessing the threeten library files
```kotlin
AndroidThreeTen.init(context) // VERY IMPORTANT
```
or
```kotlin
AndroidThreeTen.init(application) // VERY IMPORTANT
```


## Setting up the Calendar
To initialize the Calendar View

### Simple initialization
**For Compose**
```kotlin
VerticalCalendarLibrary().initialize(
    listState = rememberLazyListState(),
    mutableSelectedDate = MutableLiveData(LocalDate.now()), // Current selected date
    calendarDates = PopulatingData(
        listOf(PopulatingData()), //Pass dates that have events along with a Modifier to be used in designing indicators for the dates
        highlightedDates = Collection<CalendarDay> //Pass dates that are to be highlighted
    ),
    fullCalendarHeight = LocalContext.current.getDeviceFullHeight().dp, // Height for the calendar when it's in Full mode
    calendarTypeState = calendarType, //There are two types: CalendarType.FULL & CalendarType.MINI
    calendarVisualModifications = CalendarVisualModifications() // Various types of visual modifications for the Calendar
)
```

**Workaround for xml**
```xml
<androidx.compose.ui.platform.ComposeView
    android:id="@+id/compose_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
then, in the code file
```kotlin
compose_view.setContent{
    VerticalCalendarLibrary().initialize(
        listState = rememberLazyListState(),
        mutableSelectedDate = MutableLiveData(LocalDate.now()), // Current selected date
        calendarDates = PopulatingData(
            listOf(PopulatingData()), //Pass dates that have events along with a Modifier to be used in designing indicators for the dates
            highlightedDates = Collection<CalendarDay> //Pass dates that are to be highlighted
        ),
        fullCalendarHeight = LocalContext.current.getDeviceFullHeight().dp, // Height for the calendar when it's in Full mode
        calendarTypeState = calendarType, //There are two types: CalendarType.FULL & CalendarType.MINI
        calendarVisualModifications = CalendarVisualModifications() // Various types of visual modifications for the Calendar
    )     
}
```

## Parameters
```kotlin
data class PopulatingData(val collectionOfDates: List<IndividualData>? = null, val highlightedDates: Collection<CalendarDay>? = null)
```
`PopulatingData` takes a list of `IndividualData` object along with collection of `CalendarDay` object for Highlighting certain dates.
```kotlin
data class IndividualData(val populatedDate: Collection<CalendarDay> = emptyList(), val customModifier: Modifier = Modifier)
```
`IndividualData` takes a collection of dates for indicating events along with a custom modifier for customizing the indicator

**For reference, examples are in the VerticalCalendar app**

## Necessary Parameters
`onClickInitiatedFrom` is necessary for mitigating date selection issue when communicating between multiple calendars. (As can be seen in Multi-mode of the example app)


## Non-stable Parameters
`weekDayEnd` is currently not stable. Please use the default `WeekDayEnum.SUNDAY` for now.
`startingMonthFromCurrentMonth` is also not in use as of now. Starting & ending month are static for now. (More information given in the **Important** section)

# Important
In v1.0.0, the total number of weeks is **265 weeks** before & after from current week whereas total number of months is **60 months**  before & after from current month.

