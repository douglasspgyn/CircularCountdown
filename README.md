# Circular Countdown

[![platform](https://img.shields.io/badge/plataform-Android-brightgreen.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![GitHub version](https://badge.fury.io/gh/douglasspgyn%2FCircularCountdown.svg)](https://badge.fury.io/gh/douglasspgyn%2FCircularCountdown)
[![JitPack version](https://jitpack.io/v/douglasspgyn/CircularCountdown.svg)](https://jitpack.io/#douglasspgyn/CircularCountdown)

A custom Progress Bar that looks like a Countdown.

You can see a [Sample Project here](https://github.com/douglasspgyn/CircularCountdownSample) and learn more on the [Wiki](https://github.com/douglasspgyn/CircularCountdown/wiki).

This lib was based on [another project](https://github.com/douglasspgyn/TheFinalCountDownProject) but fully written in [Kotlin](http://kotlinlang.org/).

![](https://i.imgur.com/GQS4Qko.gif)

## XML

```xml
<douglasspgyn.com.github.circularcountdown.CircularCountdown
                android:id="@+id/circularCountdown"
                android:layout_width="72dp"
                android:layout_height="72dp"
                app:countdownBackgroundColor="@color/colorAccent"
                app:countdownForegroundColor="@color/colorPrimary"
                app:countdownTextColor="@color/colorPrimaryDark"
                app:countdownTextSize="24sp" />
```

## Circular Countdown

```kotlin
circularCountdown.create(3, 10, CircularCountdown.TYPE_SECOND)
                .listener(object : CircularListener{
                    override fun onTick(progress: Int) {

                    }
                    
                    override fun onFinish(newCycle: Boolean, cycleCount: Int) {
                        
                    }
                })
                .start()
```

## Circular Cascade Countdown

```kotlin
CircularCascadeCountdown(86405000,
                circularCountdownSeconds,
                circularCountdownMinutes,
                circularCountdownHours,
                circularCountdownDays)
                .listener(object : CascadeListener {
                    override fun onFinish() {
                        
                    }
                })
                .start()
```

## Add to your project:

You just need to add the Maven Jitpack repository on Project Gradle:
```xml
 allprojects {
    repositories {
      maven { url 'https://jitpack.io' }
    }
 }
```

and the library dependence on Module Gradle:

```xml
 dependencies {
    compile 'com.github.douglasspgyn:CircularCountdown:0.3.0'
 }
```
