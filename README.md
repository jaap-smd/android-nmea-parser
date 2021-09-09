# android-nmea-parser

Light-weight Android Java library for NMEA sentences parsing
## Supported sentences:
* GPRMC/GNRMC
* GPGGA/GNGGA
* GPGSV/GNGSV
* GPGSA/GNGSA

## NMEA Parser
flow parser build on top of the [BasicNMEAParser](src/main/java/com/github/petr_s/nmea/basic/BasicNMEAParser.kt)
that maps raw NMEA data to useful Android objects such as [Location](https://developer.android.com/reference/android/location/Location.html) and [GpsSatellite](https://developer.android.com/reference/android/location/GpsSatellite.html)

### Location parsing
To get an Android Location object you have to parse both RMC and GGA with the same time.
```kotlin
val handler = object: NMEAHandler() {
    ...
    override fun onLocation(location: Location) {

    }
    ...
}
val parser = NMEAParser(handler)
parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
parser.parse("$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F")
```

### Satellites parsing
To get a list of gps satellites you have to parse all of GSVs and at least one GSA sentence.
Since [Android GpsSatellite class](https://developer.android.com/reference/android/location/GpsSatellite.html) is inaccessible (only through reflection),
 the package level [GpsSatellite](src/main/java/com/github/petr_s/nmea/GpsSatellite.kt) is introduced.

```kotlin
val handler = object: NMEAHandler() {
    ...
    override fun onSatellites(satellites: List<GpsSatellite>) {

    }
    ...
}
val parser = NMEAParser(handler)
parser.parse("$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D")
parser.parse("$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72")
parser.parse("$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48")
parser.parse("$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B")
```


if you don't need all methods there's also an [Adapter](src/main/java/com/github/petr_s/nmea/NMEAAdapter.kt)

## Basic NMEA Parser
flow parser that allows you to access raw NMEA data

```kotlin
val handler = object: BasicNMEAHandler() {
    ...
    override fun onRMC(date: Long?, time: Long, posStatus: String, latitude: Double?, longitude: Double?, speed: Float?, direction: Float?, magVar: Float?, magVarDir: String?, modeInc: String?, isGN: Boolean) {
        
    }
    ...
}
val parser = new BasicNMEAParser(handler)
parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
```
if you don't need all methods there's also an [Adapter](src/main/java/com/github/petr_s/nmea/basic/BasicNMEAAdapter.kt)

