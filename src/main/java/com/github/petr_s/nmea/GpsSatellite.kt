package com.github.petr_s.nmea

class GpsSatellite(
    /**
     * Returns the PRN (pseudo-random number) for the satellite.
     *
     * @return PRN number
     */
    var prn: Int
) {
    var mHasEphemeris = false
    var mHasAlmanac = false
    var mUsedInFix = false

    /**
     * Returns the signal to noise ratio for the satellite.
     *
     * @return the signal to noise ratio
     */
    var snr = 0f

    /**
     * Returns the elevation of the satellite in degrees.
     * The elevation can vary between 0 and 90.
     *
     * @return the elevation in degrees
     */
    var elevation = 0f

    /**
     * Returns the azimuth of the satellite in degrees.
     * The azimuth can vary between 0 and 360.
     *
     * @return the azimuth in degrees
     */
    var azimuth = 0f
    fun setHasEphemeris(hasEphemeris: Boolean) {
        mHasEphemeris = hasEphemeris
    }

    fun setHasAlmanac(hasAlmanac: Boolean) {
        mHasAlmanac = hasAlmanac
    }

    fun setUsedInFix(usedInFix: Boolean) {
        mUsedInFix = usedInFix
    }

    /**
     * Returns true if the GPS engine has ephemeris data for the satellite.
     *
     * @return true if the satellite has ephemeris data
     */
    fun hasEphemeris(): Boolean {
        return mHasEphemeris
    }

    /**
     * Returns true if the GPS engine has almanac data for the satellite.
     *
     * @return true if the satellite has almanac data
     */
    fun hasAlmanac(): Boolean {
        return mHasAlmanac
    }

    /**
     * Returns true if the satellite was used by the GPS engine when
     * calculating the most recent GPS fix.
     *
     * @return true if the satellite was used to compute the most recent fix.
     */
    fun usedInFix(): Boolean {
        return mUsedInFix
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val satellite = other as GpsSatellite
        if (mHasEphemeris != satellite.mHasEphemeris) return false
        if (mHasAlmanac != satellite.mHasAlmanac) return false
        if (mUsedInFix != satellite.mUsedInFix) return false
        if (prn != satellite.prn) return false
        if (java.lang.Float.compare(satellite.snr, snr) != 0) return false
        return if (java.lang.Float.compare(
                satellite.elevation,
                elevation
            ) != 0
        ) false else java.lang.Float.compare(
            satellite.azimuth,
            azimuth
        ) == 0
    }

    override fun hashCode(): Int {
        var result = if (mHasEphemeris) 1 else 0
        result = 31 * result + if (mHasAlmanac) 1 else 0
        result = 31 * result + if (mUsedInFix) 1 else 0
        result = 31 * result + prn
        result = 31 * result + if (snr != +0.0f) java.lang.Float.floatToIntBits(snr) else 0
        result =
            31 * result + if (elevation != +0.0f) java.lang.Float.floatToIntBits(elevation) else 0
        result = 31 * result + if (azimuth != +0.0f) java.lang.Float.floatToIntBits(azimuth) else 0
        return result
    }

    override fun toString(): String {
        return "GpsSatellite{" +
                "mHasEphemeris=" + mHasEphemeris +
                ", mHasAlmanac=" + mHasAlmanac +
                ", mUsedInFix=" + mUsedInFix +
                ", mPrn=" + prn +
                ", mSnr=" + snr +
                ", mElevation=" + elevation +
                ", mAzimuth=" + azimuth +
                '}'
    }
}