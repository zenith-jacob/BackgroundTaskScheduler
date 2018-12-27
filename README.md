# Background Task Scheduler

An Android application which performs two background operations simultanously

  - Tracks the GPS Location
  - Monitors the percentage battery usage

Upon completion - when the size of the collected resources exceedes an input parameter, sends the collected data to the specified URL.

### Installation

Compile the project and build the .apk file using  [Android Studio](https://developer.android.com/studio/) or a single distrubution of [Gradle](https://gradle.org/).

### Usage

There are four input parameters:

| Input parameter name | Type |
| ------ | ------ | 
| GPS Interval | `Integer` (seconds) |
| Battery Interval | `Integer` (seconds) | 
| Maximum Data Capacity | `Integer` | 
| Report URL | `String` | 

# Graphical View

![Graphical View](https://i.imgur.com/JOWkFOl.png)

# Logs
You can view the logs in order to check what's happening inside the app - for instance inspect the created report data:
```sh
$ adb logcat com.zenith.scheduler
```

### Tech

Libraries used in the project:

* [GSON]
* [Unirest] 


License
----

MIT