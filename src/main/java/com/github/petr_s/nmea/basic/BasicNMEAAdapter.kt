package com.github.petr_s.nmea.basic

import com.github.petr_s.nmea.basic.BasicNMEAHandler.FixQuality
import com.github.petr_s.nmea.basic.BasicNMEAHandler.FixType

class BasicNMEAAdapter : BasicNMEAHandler {
    override fun onStart() {}
    override fun onRMC(
        date: Long?,
        time: Long?,
        posStatus: String?,
        latitude: Double?,
        longitude: Double?,
        speed: Float?,
        direction: Float?,
        magVar: Float?,
        magVarDir: String?,
        modeInc: String?,
        isGN: Boolean
    ) {
    }

    override fun onGGA(
        time: Long?,
        latitude: Double?,
        longitude: Double?,
        altitude: Float?,
        quality: FixQuality?,
        satellites: Int?,
        hdop: Float?,
        age: Float?,
        station: Int?,
        isGN: Boolean
    ) {
    }

    override fun onGSV(
        satellites: Int?,
        index: Int?,
        prn: Int?,
        elevation: Float?,
        azimuth: Float?,
        snr: Int?,
        isGN: Boolean
    ) {
    }

    override fun onGSA(
        mode: String?,
        type: FixType?,
        prns: Set<Int?>?,
        pdop: Float?,
        hdop: Float?,
        vdop: Float?,
        isGN: Boolean
    ) {
    }

    override fun onUnrecognized(sentence: String?) {}
    override fun onBadChecksum(expected: Int, actual: Int) {}
    override fun onException(e: Exception?) {}
    override fun onFinished() {}
}