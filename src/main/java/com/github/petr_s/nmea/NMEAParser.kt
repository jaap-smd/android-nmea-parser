package com.github.petr_s.nmea

import android.location.Location
import com.github.petr_s.nmea.basic.BasicNMEAHandler
import com.github.petr_s.nmea.basic.BasicNMEAHandler.FixQuality
import com.github.petr_s.nmea.basic.BasicNMEAHandler.FixType
import com.github.petr_s.nmea.basic.BasicNMEAParser
import java.util.*

class NMEAParser @JvmOverloads constructor(
    private val handler: NMEAHandler?,
    private val locationFactory: LocationFactory = object : LocationFactory() {
        override fun newLocation(): Location? {
            return Location(LOCATION_PROVIDER_NAME)
        }
    }
) : BasicNMEAHandler {
    private val basicParser: BasicNMEAParser
    private var location: Location? = null
    private var lastTime: Long = 0
    private var flags = 0
    private var satellitesCount = 0
    private val tempSatellites = arrayOfNulls<GpsSatellite>(SATELLITES_COUNT)
    private var activeSatellites: Set<Int?>? = null
    @Synchronized
    fun parse(sentence: String?) {
        basicParser.parse(sentence)
    }

    private fun resetLocationState() {
        flags = 0
        lastTime = 0
    }

    private fun newLocation(time: Long) {
        if (location == null || time != lastTime) {
            location = locationFactory.newLocation()
            resetLocationState()
        }
    }

    private fun yieldLocation(time: Long, flag: Int) {
        if (flags or flag and LOCATION_FLAGS == LOCATION_FLAGS) {
            handler!!.onLocation(location)
            resetLocationState()
        } else {
            flags = flags or flag
            lastTime = time
        }
    }

    private fun hasAllSatellites(): Boolean {
        for (i in 0 until satellitesCount) {
            if (tempSatellites[i] == null) {
                return false
            }
        }
        return true
    }

    private fun yieldSatellites() {
        if (satellitesCount > 0 && hasAllSatellites() && activeSatellites != null) {
            for (satellite in tempSatellites) {
                if (satellite == null) {
                    break
                } else {
                    satellite.setUsedInFix(activeSatellites!!.contains(satellite.prn))
                    satellite.setHasAlmanac(true) // TODO: ...
                    satellite.setHasEphemeris(true) // TODO: ...
                }
            }
            handler!!.onSatellites(Arrays.asList(*Arrays.copyOf(tempSatellites, satellitesCount)))
            Arrays.fill(tempSatellites, null)
            activeSatellites = null
            satellitesCount = 0
        }
    }

    private fun newSatellite(
        index: Int,
        count: Int,
        prn: Int,
        elevation: Float,
        azimuth: Float,
        snr: Int
    ) {
        if (count != satellitesCount) {
            satellitesCount = count
        }
        val satellite = GpsSatellite(prn)
        satellite.azimuth = azimuth
        satellite.elevation = elevation
        satellite.snr = snr.toFloat()
        tempSatellites[index] = satellite
    }

    @Synchronized
    override fun onStart() {
        handler!!.onStart()
    }

    @Synchronized
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
        faa: String?,
        isGN: Boolean
    ) {
        time?.let {
            newLocation(time)
            location!!.time = date?.or(time) ?: time
            location!!.speed = speed ?: -1F
            location!!.bearing = direction ?: -1F
            yieldLocation(time, FLAG_RMC)
        }
    }

    @Synchronized
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
        time?.let {
            newLocation(time)
            location!!.latitude = latitude ?: 0.0
            location!!.longitude = longitude ?: 0.0
            location!!.altitude = altitude?.toDouble() ?: 0.0
            location!!.accuracy = hdop?.times(4.0f) ?: 0f
            yieldLocation(time, FLAG_GGA)
        }
    }

    @Synchronized
    override fun onGSV(
        satellites: Int?,
        index: Int?,
        prn: Int?,
        elevation: Float?,
        azimuth: Float?,
        snr: Int?,
        isGN: Boolean
    ) {
        if(index!= null && satellites != null && prn != null && elevation != null && azimuth != null && snr != null)
            newSatellite(index, satellites, prn, elevation, azimuth, snr)
        yieldSatellites()
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
        activeSatellites = prns
        yieldSatellites()
    }

    @Synchronized
    override fun onUnrecognized(sentence: String?) {
        handler!!.onUnrecognized(sentence)
    }

    @Synchronized
    override fun onBadChecksum(expected: Int, actual: Int) {
        handler!!.onBadChecksum(expected, actual)
    }

    @Synchronized
    override fun onException(e: Exception?) {
        handler!!.onException(e)
    }

    @Synchronized
    override fun onFinished() {
        handler!!.onFinish()
    }

    companion object {
        const val LOCATION_PROVIDER_NAME = "nmea-parser"
        private const val FLAG_RMC = 1
        private const val FLAG_GGA = 2
        private const val LOCATION_FLAGS = FLAG_RMC or FLAG_GGA
        private const val SATELLITES_COUNT = 24
    }

    init {
        basicParser = BasicNMEAParser(this)
        if (handler == null) {
            throw NullPointerException()
        }
    }
}