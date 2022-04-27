# VerticalCalendarLibrary
Vertical Calendar library originally made for Bonzun IVF using **Jetpack Compose**

[ ![Download](https://api.bintray.com/packages/binishmanandhar23/PhotoEditorX/com.binish.photoeditorx/images/download.svg?version=1.0.5) ](https://bintray.com/binishmanandhar23/PhotoEditorX/com.binish.photoeditorx/1.0.5/link) ![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)

Helps you add Stickers, Texts, Images & Emoji's over images and customize them

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

##Initial Setup
I'm using org.threeten.bp library for working with dates which is why the library must be initialized in the Application file or your MainActivity's **onCreate()** before accessing the threeten library files
```kotlin
AndroidThreeTen.init(context) // VERY IMPORTANT
```
or
```kotlin
AndroidThreeTen.init(application) // VERY IMPORTANT
```


## Setting up the View
First we need to add `PhotoEditorView` in out xml layout

```xml
<com.binish.photoeditorx.photoeditor.PhotoEditorView
        android:id="@+id/photoEditorView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:photo_src="@drawable/nepal_wallpaper"/>
```

We can define our drawable or color resource directly using `app:photo_src`

Or,

We can set the image programmatically by getting source from `PhotoEditorView` which will return a `ImageView` so that we can load image from resources,file or (Picasso/Glide)


```kotlin
val mPhotoEditorView = findViewById(R.id.photoEditorView)
mPhotoEditorView.source.setImageResource(R.drawable.nepal_wallpaper)
```

