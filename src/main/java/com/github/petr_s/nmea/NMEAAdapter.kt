package com.github.petr_s.nmea

import android.location.Location

class NMEAAdapter : NMEAHandler {
    override fun onStart() {}
    override fun onLocation(location: Location?) {}
    override fun onSatellites(satellites: List<GpsSatellite?>?) {}
    override fun onUnrecognized(sentence: String?) {}
    override fun onBadChecksum(expected: Int, actual: Int) {}
    override fun onException(e: Exception?) {}
    override fun onFinish() {}
}