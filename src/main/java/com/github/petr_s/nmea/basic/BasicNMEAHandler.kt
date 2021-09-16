package com.github.petr_s.nmea.basic

interface BasicNMEAHandler {
    fun onStart()

    /***
     * Called on GPRMC and GNRMC parsed.
     *
     * @param date      milliseconds since midnight, January 1, 1970 UTC.
     * @param time      actual UTC time (without date)
     * @param posStatus position status (A = data valid, V = data invalid)
     * @param latitude  angular y position on the Earth.
     * @param longitude angular x position on the Earth.
     * @param speed     in meters per second.
     * @param direction angular bearing value to the North.
     * @param magVar    magnetic variation, degrees. Note: that this field is the actual magnetic variation and will always be positive.
     * @param magVarDir direction of magnetic variation E/W: Easterly variation (E) subtracts from True course, Westerly variation (W) adds to True course.
     * @param modeInc   positioning system mode indicator: A - Autonomous, D - Differential, E - Estimated (dead reckoning) mode, M - Manual input, N - Data not valid
     * @param isGN      returns true if sentence was GNRMC, false is sentence was GPRMC
     */
    fun onRMC(
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
    )

    /***
     * Called on GPGGA and GNGGA parsed.
     *
     * @param time        actual UTC time (without date)
     * @param latitude    angular y position on the Earth.
     * @param longitude   angular x position on the Earth.
     * @param altitude    altitude in meters above corrected geoid
     * @param quality     fix-quality type [FixQuality]
     * @param satellites  actual number of satellites
     * @param hdop        horizontal dilution of precision
     * @param age         age
     * @param isGN        returns true if sentence was GNGGA, false is sentence was GPGGA
     */
    fun onGGA(
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
    )

    /***
     * Called on GPGSV parsed.
     * Note that single nmea sentence contains up to 4 satellites therefore you can receive 4 calls per sentence.
     *
     * @param satellites total number of satellites
     * @param index      index of satellite
     * @param prn        pseudo-random noise number
     * @param elevation  elevation in degrees
     * @param azimuth    azimuth in degrees
     * @param snr        signal to noise ratio
     */
    fun onGSV(satellites: Int?,
              index: Int?,
              prn: Int?,
              elevation: Float?,
              azimuth: Float?,
              snr: Int?,
              isGN: Boolean)

    /***
     * Called on GPGSA parsed.
     *
     * @param mode where the mode of operation is automatically or manually set, A = Automatic 2D/3D, M = Manual, forced to operate in 2D or 3D
     * @param type type of fix
     * @param prns set of satellites used for the current fix
     * @param pdop position dilution of precision
     * @param hdop horizontal dilution of precision
     * @param vdop vertical dilution of precision
     */
    fun onGSA(
        mode: String?,
        type: FixType?,
        prns: Set<Int?>?,
        pdop: Float?,
        hdop: Float?,
        vdop: Float?,
        isGN: Boolean
    )

    fun onUnrecognized(sentence: String?)
    fun onBadChecksum(expected: Int, actual: Int)
    fun onException(e: Exception?)
    fun onFinished()

    enum class FixQuality(val value: Int) {
        Invalid(0),
        GPS(1),
        DGPS(2),
        PPS(3),
        IRTK(4),
        FRTK(5),
        Estimated(6),
        Manual(7),
        Simulation(8);
    }

    enum class FixType(val value: Int) {
        Invalid(0),
        None(1),
        Fix2D(2),
        Fix3D(3);
    }
}