package com.github.petr_s.nmea

import android.location.Location
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.runners.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class NMEAParserTest {
    @Spy
    var handler: NMEAHandler = NMEAAdapter()

    @Mock
    var location: Location? = null

    @Mock
    var locationFactory: LocationFactory? = null
    var parser: NMEAParser? = null
    @Before
    fun setUp() {
        parser = NMEAParser(handler, locationFactory!!)
    }

    @Test(expected = NullPointerException::class)
    @Throws(Exception::class)
    fun testConstructorNull() {
        NMEAParser(null)
    }

    @Test(expected = NullPointerException::class)
    @Throws(Exception::class)
    fun testParseNull() {
        parser!!.parse(null)
    }

    @Test
    @Throws(Exception::class)
    fun testParseLocationRMCGGA() {
        Mockito.`when`(locationFactory!!.newLocation()).thenReturn(location)
        parser!!.parse("\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
        parser!!.parse("\$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F")
        Mockito.verify(handler, Mockito.times(2)).onStart()
        Mockito.verify(handler, Mockito.times(2)).onFinish()
        Mockito.verify(handler).onLocation(location)
        Mockito.verifyNoMoreInteractions(handler)
        Mockito.verify(location)!!.time = ArgumentMatchers.eq(1460954639384L)
        Mockito.verify(location)!!.latitude = ArgumentMatchers.doubleThat(Helper.roughlyEq(50.07914))
        Mockito.verify(location)!!.longitude =
            ArgumentMatchers.doubleThat(Helper.roughlyEq(14.39825))
        Mockito.verify(location)!!.altitude =
            ArgumentMatchers.doubleThat(Helper.roughlyEq(240.2))
        Mockito.verify(location)!!.accuracy =
            ArgumentMatchers.floatThat(Helper.roughlyEq(6.8f))
        Mockito.verify(location)!!.speed = ArgumentMatchers.floatThat(Helper.roughlyEq(0.02057f))
        Mockito.verify(location)!!.bearing = ArgumentMatchers.floatThat(Helper.roughlyEq(36.97f))
        Mockito.verifyNoMoreInteractions(location)
        Mockito.verify(locationFactory)!!.newLocation()
        Mockito.verifyNoMoreInteractions(locationFactory)
    }

    @Test
    @Throws(Exception::class)
    fun testParseLocationGGARMC() {
        Mockito.`when`(locationFactory!!.newLocation()).thenReturn(location)
        parser!!.parse("\$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F")
        parser!!.parse("\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
        Mockito.verify(handler, Mockito.times(2)).onStart()
        Mockito.verify(handler, Mockito.times(2)).onFinish()
        Mockito.verify(handler).onLocation(location)
        Mockito.verifyNoMoreInteractions(handler)
        Mockito.verify(location)?.time = ArgumentMatchers.eq(1460954639384L)
        Mockito.verify(location)?.latitude =
            ArgumentMatchers.doubleThat(Helper.roughlyEq(50.079141))
        Mockito.verify(location)?.longitude =
            ArgumentMatchers.doubleThat(Helper.roughlyEq(14.39825))
        Mockito.verify(location)?.altitude =
            ArgumentMatchers.doubleThat(Helper.roughlyEq(240.2))
        Mockito.verify(location)?.accuracy =
            ArgumentMatchers.floatThat(Helper.roughlyEq(6.8f))
        Mockito.verify(location)?.speed =
            ArgumentMatchers.floatThat(Helper.roughlyEq(0.02057f))
        Mockito.verify(location)?.bearing =
            ArgumentMatchers.floatThat(Helper.roughlyEq(36.97f))
        Mockito.verifyNoMoreInteractions(location)
        Mockito.verify(locationFactory)?.newLocation()
        Mockito.verifyNoMoreInteractions(locationFactory)
    }

    @Test
    @Throws(Exception::class)
    fun testParseLocationRMCGGADiffTime() {
        Mockito.`when`(locationFactory!!.newLocation()).thenReturn(location)
        parser!!.parse("\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
        parser!!.parse("\$GPGGA,163408.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*50")
        Mockito.verify(handler, Mockito.times(2)).onStart()
        Mockito.verify(handler, Mockito.times(2)).onFinish()
        Mockito.verifyNoMoreInteractions(handler)
        Mockito.verify(locationFactory, Mockito.times(2))?.newLocation()
        Mockito.verifyNoMoreInteractions(locationFactory)
    }

    @Test
    @Throws(Exception::class)
    fun testParseLocationGGARMCDiffTime() {
        Mockito.`when`(locationFactory!!.newLocation()).thenReturn(location)
        parser!!.parse("\$GPGGA,163408.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*50")
        parser!!.parse("\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
        Mockito.verify(handler, Mockito.times(2)).onStart()
        Mockito.verify(handler, Mockito.times(2)).onFinish()
        Mockito.verifyNoMoreInteractions(handler)
        Mockito.verify(locationFactory, Mockito.times(2))?.newLocation()
        Mockito.verifyNoMoreInteractions(locationFactory)
    }

    @Test
    @Throws(Exception::class)
    fun testParseLocationGGAGGA() {
        Mockito.`when`(locationFactory!!.newLocation()).thenReturn(location)
        parser!!.parse("\$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F")
        parser!!.parse("\$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F")
        Mockito.verify(handler, Mockito.times(2)).onStart()
        Mockito.verify(handler, Mockito.times(2)).onFinish()
        Mockito.verifyNoMoreInteractions(handler)
        Mockito.verify(locationFactory)?.newLocation()
        Mockito.verifyNoMoreInteractions(locationFactory)
    }

    @Test
    @Throws(Exception::class)
    fun testParseLocationRMCRMC() {
        Mockito.`when`(locationFactory!!.newLocation()).thenReturn(location)
        parser!!.parse("\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
        parser!!.parse("\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
        Mockito.verify(handler, Mockito.times(2)).onStart()
        Mockito.verify(handler, Mockito.times(2)).onFinish()
        Mockito.verifyNoMoreInteractions(handler)
        Mockito.verify(locationFactory)?.newLocation()
        Mockito.verifyNoMoreInteractions(locationFactory)
    }

    @Test
    @Throws(Exception::class)
    fun testParseLocationRMCGGAGGASameTime() {
        Mockito.`when`(locationFactory!!.newLocation()).thenReturn(
            location, Mockito.mock(
                Location::class.java
            )
        )
        parser!!.parse("\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
        parser!!.parse("\$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F")
        parser!!.parse("\$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F")
        Mockito.verify(handler, Mockito.times(3)).onStart()
        Mockito.verify(handler, Mockito.times(3)).onFinish()
        Mockito.verify(handler).onLocation(location)
        Mockito.verifyNoMoreInteractions(handler)
        Mockito.verify(location)?.time = ArgumentMatchers.eq(1460954639384L)
        Mockito.verify(location)?.latitude = ArgumentMatchers.doubleThat(Helper.roughlyEq(50.079141))
        Mockito.verify(location)?.longitude = ArgumentMatchers.doubleThat(Helper.roughlyEq(14.39825))
        Mockito.verify(location)?.altitude = ArgumentMatchers.doubleThat(Helper.roughlyEq(240.2))
        Mockito.verify(location)?.accuracy = ArgumentMatchers.floatThat(Helper.roughlyEq(6.8f))
        Mockito.verify(location)?.speed = ArgumentMatchers.floatThat(Helper.roughlyEq(0.02057f))
        Mockito.verify(location)?.bearing = ArgumentMatchers.floatThat(Helper.roughlyEq(36.97f))
        Mockito.verifyNoMoreInteractions(location)
        Mockito.verify(locationFactory, Mockito.times(2))?.newLocation()
        Mockito.verifyNoMoreInteractions(locationFactory)
    }

    @Test
    @Throws(Exception::class)
    fun testParseLocationRMCGGARMCSameTime() {
        Mockito.`when`(locationFactory!!.newLocation()).thenReturn(
            location, Mockito.mock(
                Location::class.java
            )
        )
        parser!!.parse("\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
        parser!!.parse("\$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F")
        parser!!.parse("\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38")
        Mockito.verify(handler, Mockito.times(3)).onStart()
        Mockito.verify(handler, Mockito.times(3)).onFinish()
        Mockito.verify(handler).onLocation(location)
        Mockito.verifyNoMoreInteractions(handler)
        Mockito.verify(location)?.time = ArgumentMatchers.eq(1460954639384L)
        Mockito.verify(location)?.speed = ArgumentMatchers.eq(0.020577759f)
        Mockito.verify(location)?.bearing = ArgumentMatchers.eq(36.97f)
        Mockito.verify(location)?.latitude = ArgumentMatchers.eq(50.079141664505)
        Mockito.verify(location)?.longitude = ArgumentMatchers.eq(14.398259989420573)
        Mockito.verify(location)?.accuracy = ArgumentMatchers.eq(6.8f)
        //TODO
//        Mockito.verify(location)?.altitude = ArgumentMatchers.eq(240.20001)
//        Mockito.verifyNoMoreInteractions(location)
        Mockito.verify(locationFactory, Mockito.times(2))?.newLocation()
        Mockito.verifyNoMoreInteractions(locationFactory)
    }

    private fun newSatellite(
        prn: Int,
        elevation: Float,
        azimuth: Float,
        snr: Int,
        fix: Boolean
    ): GpsSatellite {
        val satellite = GpsSatellite(prn)
        satellite.azimuth = azimuth
        satellite.elevation = elevation
        satellite.snr = snr.toFloat()
        satellite.setUsedInFix(fix)
        satellite.setHasAlmanac(true)
        satellite.setHasEphemeris(true)
        return satellite
    }

    @Test
    @Throws(Exception::class)
    fun testParseSatelliteGSVGSA() {
        parser!!.parse("\$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D")
        parser!!.parse("\$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B")
        Mockito.verify(handler, Mockito.times(2)).onStart()
        Mockito.verify(handler, Mockito.times(2)).onFinish()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseSatellite3GSVGGA() {
        parser!!.parse("\$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D")
        parser!!.parse("\$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72")
        parser!!.parse("\$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48")
        parser!!.parse("\$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B")
        Mockito.verify(handler, Mockito.times(4)).onStart()
        Mockito.verify(handler, Mockito.times(4)).onFinish()
        Mockito.verify(handler).onSatellites(
            ArgumentMatchers.argThat(
                Helper.eq(
                    listOf(
                        newSatellite(29, 86.0f, 273.0f, 30, true),
                        newSatellite(25, 60.0f, 110.0f, 38, true),
                        newSatellite(31, 52.0f, 278.0f, 47, true),
                        newSatellite(2, 28.0f, 50.0f, 39, true),
                        newSatellite(12, 23.0f, 110.0f, 34, true),
                        newSatellite(26, 18.0f, 295.0f, 29, true),
                        newSatellite(21, 17.0f, 190.0f, 30, true),
                        newSatellite(5, 11.0f, 92.0f, 25, true),
                        newSatellite(14, 2.0f, 232.0f, 13, false),
                        newSatellite(23, 2.0f, 346.0f, 12, false),
                        newSatellite(20, 1.0f, 135.0f, 13, false)

                    )
                )
            ) as List<GpsSatellite?>?
        )
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseSatelliteGGA3GSV() {
        parser!!.parse("\$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B")
        parser!!.parse("\$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D")
        parser!!.parse("\$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72")
        parser!!.parse("\$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48")
        Mockito.verify(handler, Mockito.times(4)).onStart()
        Mockito.verify(handler, Mockito.times(4)).onFinish()
        Mockito.verify(handler).onSatellites(
            ArgumentMatchers.argThat(
                Helper.eq(
                    listOf<GpsSatellite?>(
                        newSatellite(29, 86.0f, 273.0f, 30, true),
                        newSatellite(25, 60.0f, 110.0f, 38, true),
                        newSatellite(31, 52.0f, 278.0f, 47, true),
                        newSatellite(2, 28.0f, 50.0f, 39, true),
                        newSatellite(12, 23.0f, 110.0f, 34, true),
                        newSatellite(26, 18.0f, 295.0f, 29, true),
                        newSatellite(21, 17.0f, 190.0f, 30, true),
                        newSatellite(5, 11.0f, 92.0f, 25, true),
                        newSatellite(14, 2.0f, 232.0f, 13, false),
                        newSatellite(23, 2.0f, 346.0f, 12, false),
                        newSatellite(20, 1.0f, 135.0f, 13, false)

                    )
                )
            ) as List<GpsSatellite?>?
        )
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseSatellite2GSVGGAGSV() {
        parser!!.parse("\$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D")
        parser!!.parse("\$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72")
        parser!!.parse("\$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B")
        parser!!.parse("\$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48")
        Mockito.verify(handler, Mockito.times(4)).onStart()
        Mockito.verify(handler, Mockito.times(4)).onFinish()
        Mockito.verify(handler).onSatellites(
            ArgumentMatchers.argThat(
                Helper.eq(
                    listOf(
                        newSatellite(29, 86.0f, 273.0f, 30, true),
                        newSatellite(25, 60.0f, 110.0f, 38, true),
                        newSatellite(31, 52.0f, 278.0f, 47, true),
                        newSatellite(2, 28.0f, 50.0f, 39, true),
                        newSatellite(12, 23.0f, 110.0f, 34, true),
                        newSatellite(26, 18.0f, 295.0f, 29, true),
                        newSatellite(21, 17.0f, 190.0f, 30, true),
                        newSatellite(5, 11.0f, 92.0f, 25, true),
                        newSatellite(14, 2.0f, 232.0f, 13, false),
                        newSatellite(23, 2.0f, 346.0f, 12, false),
                        newSatellite(20, 1.0f, 135.0f, 13, false)

                    )
                )
            ) as List<GpsSatellite?>?
        )
        Mockito.verifyNoMoreInteractions(handler)
    }
}