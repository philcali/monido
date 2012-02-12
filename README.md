# Monido

Pure Scala monitoring service that comes with a file monitoring reference implementation.
Monido was actually a spawn from my desire to apply the [cake pattern] in some project.

## Goals

  * Flexible
  * Easy to extend
  * Straight forward

## What's a Mondio?

A `Monido` is short for "Monitor this thing, and do something!" It's completely made-up.
I'm actually making it up as I type...


A typical `Monido` is comprised of three components. These are:

  * `PulsatingComponent`
  * `MonitorComponent`
  * `ListeningComponent` (optional)

## Process

A `PulsatingComponent` is something that wakes the `MonitorComponent` to do something. 
A PulsatingComponent could wake it at set intervals, once a day, manually, etc. 
The `MonitorComponent` will simply monitor whatever it was told to monitor (The `FileMonido` 
reference implementation monitors the file system.) Optionally, the Monitor can notify a client 
of a change (or anything else really) by making use of the `ListeningComponent`.

Lots of moving parts that have arbitrary dependencies make it a great candidate for some DI.

[cake pattern]: http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di.html

## App Installation

The Monido project has been conscripted, so a simple:
    
    cs philcali/monido

Is all that's needed to install it. Now you can do things like:

    monido some/path -e ls -l

## Library

For simple monitoring services in your app, include the `monido-core` in your build
process:

```scala
libraryDependencies += "org.github.philcali" %% "monido-core" % "0.1.2"
```
