Hacktoolkit for Android
=======================

This library project has many useful wrappers and reusable components.

Built-in support for popular APIs:
* [Parse](http://parse.com)
* [Google Maps](https://developers.google.com/maps/)
* [Facebook SDK](https://developers.google.com/maps/)

## Requirements

* [Parse SDK](https://parse.com/docs/downloads/) (included in library)
* [Google Play Services](http://developer.android.com/google/play-services/setup.html)
* [Facebook SDK Project](https://developers.facebook.com/docs/android)

## Sample Projects built using Hacktoolkit for Android

* [Hello World](https://github.com/hacktoolkit/htk-android-HelloWorld)
* [Simple Todo List App](https://github.com/hacktoolkit/htk-android-SimpleTodo)

## Useful Components

### Activities (`/activities/*`)

* `HTKSplashScreenActivity.java` - an abstract class for a splash screen activity
  Displays the view and hides the system UI for a number of seconds (configurable) before switching

### User (`/user/*`)

* `HTKUser.java` - a user model

### Utilties (`/utils/*`)

* `HTKUtils.java` - contains a lot of useful functionality that can be called as one-liners,
  e.g. `switchActivity`, `showSoftKeyboard`, `hideSoftKeyboard`
