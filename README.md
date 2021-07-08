# GeoEvent Timetree Processor

This custom processor provides the capability to create a line out of a cache of historical points. Incoming GeoEvents are added to a queue and held in memory in a time-ordered queue. Every time a new event is received, a line is generated out of a number of points based on a point count or time window.

This type of processor can be useful where you want to show the historical location of a vehicle over a small time window (e.g. last 5 points or minutes).

NOTE: This processor is not appropriate for event streams with a large number of unique TRACK_IDs that will result in a large number of events being held in memory. When deploying this processor, ensure the system has adequate memory and monitor memory usage to ensure proper operation.

![Example](geoevent-timetree-processor.png?raw=true)

## Usage

* The following parameters are supported:
  * `Use Event Count?` Yes will use a count to determine how many events are used to create the line. No will use a time window (no limit on the number of points used, so long as they are within the time window).
   All events within the count or time window are added to queue and held in memory. Each track id has its own queue.
  * `Event Window Size` specifies either the number of events to hold in the cache or the time window to use.
  * `Event Window Time Unit` specifies the time unit for the Event Window Size value when Use Event Count?=No.
  * `Event Window Time Field` Choose the field that the event time window will use to evaluate an event (RECEIVE_TIME, TIME_START, or TIME_END).
  * `Clear Cache?` Should all cached events be cleared. 
  
<p> Examples:
<p><b>Use Event Count = No, Event Window Size = 100 seconds, Time Field = TIME_START</b><br>These settings will create a line out of all events received in the last 100 seconds for a specific track id.
<p><b>Use Event Count = Yes, Event Window Size = 5</b><br>These settings will create a line out of the last 5 events received for a specific track id.

## Features
* GeoEvent Timetree Processor

## Instructions

Building the source code:

1. Make sure Maven and ArcGIS GeoEvent Server SDK are installed on your machine.
2. Run 'mvn install -Dcontact.address=[YourContactEmailAddress]'

Installing the built jar files:

1. Copy the *.jar files under the 'target' sub-folder(s) into the [ArcGIS-GeoEvent-Server-Install-Directory]/deploy folder.

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## Support

This component is not officially supported as an Esri product. The source code is available under the Apache License. 

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).


