package com.github.petr_s.nmea

import android.location.Location

interface NMEAHandler {
    fun onStart()
    fun onLocation(location: Location?)
    fun onSatellites(satellites: List<GpsSatellite?>?)
    fun onUnrecognized(sentence: String?)
    fun onBadChecksum(expected: Int, actual: Int)
    fun onException(e: Exception?)
    fun onFinish()
}