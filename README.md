# VerticalCalendarLibrary
Vertical Calendar library originally made for Bonzun IVF

[ ![Download](https://api.bintray.com/packages/binishmanandhar23/PhotoEditorX/com.binish.photoeditorx/images/download.svg?version=1.0.5) ](https://bintray.com/binishmanandhar23/PhotoEditorX/com.binish.photoeditorx/1.0.5/link) ![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg)

Helps you add Stickers, Texts, Images & Emoji's over images and customize them

## Installation

Add the following dependencies in the gradle file of your app module to get started:  

Gradle
```kotlin
implementation 'io.github.binishmanandhar23.photoeditorx:photoeditorx:1.0.5'
```
Maven
```xml
<dependency>
  <groupId>io.github.binishmanandhar23.photoeditorx</groupId>
  <artifactId>photoeditorx</artifactId>
  <version>1.0.5</version>
  <type>pom</type>
</dependency>
```

or if you want to further customize the module, simply import it.

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

