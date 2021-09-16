package com.github.petr_s.nmea.basic

import com.github.petr_s.nmea.Helper
import com.github.petr_s.nmea.basic.BasicNMEAHandler.FixQuality
import com.github.petr_s.nmea.basic.BasicNMEAHandler.FixType
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.runners.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class BasicNMEAParserTest {
    @Spy
    var handler: BasicNMEAHandler = BasicNMEAAdapter()
    @Test(expected = NullPointerException::class)
    @Throws(Exception::class)
    fun testConstructorNull() {
        BasicNMEAParser(null)
    }

    @Test(expected = NullPointerException::class)
    @Throws(Exception::class)
    fun testParseNullSentence() {
        BasicNMEAParser(handler).parse(null)
    }

    @Test
    @Throws(Exception::class)
    fun testParseEmpty() {
        BasicNMEAParser(handler).parse("")
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onUnrecognized("")
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGPRMC() {
//        val sentence = "\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38"
        val sentence = "\$GNRMC,201818.00,A,3250.193717,N,11707.686287,W,0.0,,140921,12.5,E,A,V*5D"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onRMC(
            ArgumentMatchers.eq(1460937600000L),
            ArgumentMatchers.eq(59647000L),
            ArgumentMatchers.isNull(String::class.java),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(50.07914)),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(14.39825)),
            ArgumentMatchers.floatThat(Helper.roughlyEq(0.02057f)),
            ArgumentMatchers.floatThat(Helper.roughlyEq(36.97f)),
            ArgumentMatchers.isNull(Float::class.java),
            ArgumentMatchers.isNull(String::class.java),
            ArgumentMatchers.isNull(String::class.java),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGPRMC_2_3() {
        val sentence = "\$GPRMC,093933.40,A,5004.52493,N,01424.28771,E,0.277,,130616,,,A*76"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onRMC(
            ArgumentMatchers.eq(1465776000000L),
            ArgumentMatchers.eq(34773400L),
            ArgumentMatchers.isNull(String::class.java),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(50.075415)),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(14.404795)),
            ArgumentMatchers.floatThat(Helper.roughlyEq(0.142501f)),
            ArgumentMatchers.floatThat(Helper.roughlyEq(0.0f)),
            ArgumentMatchers.isNull(Float::class.java),
            ArgumentMatchers.isNull(String::class.java),
            ArgumentMatchers.eq("A"),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGNRMC() {
        val sentence = "\$GNRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*26"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onRMC(
            ArgumentMatchers.eq(1460937600000L),
            ArgumentMatchers.eq(59647000L),
            ArgumentMatchers.isNull(String::class.java),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(50.07914)),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(14.39825)),
            ArgumentMatchers.floatThat(Helper.roughlyEq(0.02057f)),
            ArgumentMatchers.floatThat(Helper.roughlyEq(36.97f)),
            ArgumentMatchers.isNull(Float::class.java),
            ArgumentMatchers.isNull(String::class.java),
            ArgumentMatchers.isNull(String::class.java),
            ArgumentMatchers.eq(true)
        )
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGPRMCBadChecksum() {
        val sentence = "\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*42"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onBadChecksum(66, 56)
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGPRMCEOL() {
        val sentence = "\n\$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*42"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onUnrecognized(sentence)
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGPGGA() {
        val sentence =
            "\$GPGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*5F"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onGGA(
            ArgumentMatchers.eq(59647000L),
            ArgumentMatchers.eq(50.079141664505),
            ArgumentMatchers.eq(14.398259989420573),
            ArgumentMatchers.eq(240.20001f),
            ArgumentMatchers.eq(FixQuality.GPS),
            ArgumentMatchers.eq(7),
            ArgumentMatchers.eq(1.7f),
            ArgumentMatchers.isNull(),
            ArgumentMatchers.eq(0),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGNGGA() {
        val sentence =
            "\$GNGGA,163407.000,5004.7485,N,01423.8956,E,1,07,1.7,285.7,M,45.5,M,,0000*41"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onGGA(
            ArgumentMatchers.eq(59647000L),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(50.07914)),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(14.39825)),
            ArgumentMatchers.floatThat(Helper.roughlyEq(240.2f)),
            ArgumentMatchers.eq(FixQuality.GPS),
            ArgumentMatchers.eq(7),
            ArgumentMatchers.floatThat(Helper.roughlyEq(1.7f)),
            ArgumentMatchers.isNull(Float::class.java),
            ArgumentMatchers.eq(0),
            ArgumentMatchers.eq(true)
        )
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGPGGANegativeGeoid() {
        val sentence =
            "\$GPGGA,214213.00,3249.263664,N,11710.592247,W,1,11,0.6,102.2,M,-26.0,M,,*51"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onGGA(
            ArgumentMatchers.eq(78133000L),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(32.82106)),
            ArgumentMatchers.doubleThat(Helper.roughlyEq(-117.17653)),
            ArgumentMatchers.floatThat(Helper.roughlyEq(128.2f)),
            ArgumentMatchers.eq(FixQuality.GPS),
            ArgumentMatchers.eq(11),
            ArgumentMatchers.floatThat(Helper.roughlyEq(0.6f)),
            ArgumentMatchers.isNull(Float::class.java),
            ArgumentMatchers.isNull(Int::class.java),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGPGSVSingle() {
        val sentence = "\$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(0),
            ArgumentMatchers.eq(29),
            ArgumentMatchers.eq(86.0f),
            ArgumentMatchers.eq(273.0f),
            ArgumentMatchers.eq(30),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(1),
            ArgumentMatchers.eq(25),
            ArgumentMatchers.eq(60.0f),
            ArgumentMatchers.eq(110.0f),
            ArgumentMatchers.eq(38),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(2),
            ArgumentMatchers.eq(31),
            ArgumentMatchers.eq(52.0f),
            ArgumentMatchers.eq(278.0f),
            ArgumentMatchers.eq(47),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(3),
            ArgumentMatchers.eq(2),
            ArgumentMatchers.eq(28.0f),
            ArgumentMatchers.eq(50.0f),
            ArgumentMatchers.eq(39),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGPGSVFull() {
        val parser = BasicNMEAParser(handler)
        parser.parse("\$GPGSV,3,1,11,29,86,273,30,25,60,110,38,31,52,278,47,02,28,050,39*7D")
        parser.parse("\$GPGSV,3,2,11,12,23,110,34,26,18,295,29,21,17,190,30,05,11,092,25*72")
        parser.parse("\$GPGSV,3,3,11,14,02,232,13,23,02,346,12,20,01,135,13*48")
        Mockito.verify(handler, Mockito.times(3)).onStart()
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(0),
            ArgumentMatchers.eq(29),
            ArgumentMatchers.eq(86.0f),
            ArgumentMatchers.eq(273.0f),
            ArgumentMatchers.eq(30),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(1),
            ArgumentMatchers.eq(25),
            ArgumentMatchers.eq(60.0f),
            ArgumentMatchers.eq(110.0f),
            ArgumentMatchers.eq(38),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(2),
            ArgumentMatchers.eq(31),
            ArgumentMatchers.eq(52.0f),
            ArgumentMatchers.eq(278.0f),
            ArgumentMatchers.eq(47),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(3),
            ArgumentMatchers.eq(2),
            ArgumentMatchers.eq(28.0f),
            ArgumentMatchers.eq(50.0f),
            ArgumentMatchers.eq(39),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(4),
            ArgumentMatchers.eq(12),
            ArgumentMatchers.eq(23.0f),
            ArgumentMatchers.eq(110.0f),
            ArgumentMatchers.eq(34),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(5),
            ArgumentMatchers.eq(26),
            ArgumentMatchers.eq(18.0f),
            ArgumentMatchers.eq(295.0f),
            ArgumentMatchers.eq(29),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(6),
            ArgumentMatchers.eq(21),
            ArgumentMatchers.eq(17.0f),
            ArgumentMatchers.eq(190.0f),
            ArgumentMatchers.eq(30),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(7),
            ArgumentMatchers.eq(5),
            ArgumentMatchers.eq(11.0f),
            ArgumentMatchers.eq(92.0f),
            ArgumentMatchers.eq(25),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(8),
            ArgumentMatchers.eq(14),
            ArgumentMatchers.eq(2.0f),
            ArgumentMatchers.eq(232.0f),
            ArgumentMatchers.eq(13),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(9),
            ArgumentMatchers.eq(23),
            ArgumentMatchers.eq(2.0f),
            ArgumentMatchers.eq(346.0f),
            ArgumentMatchers.eq(12),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onGSV(
            ArgumentMatchers.eq(11),
            ArgumentMatchers.eq(10),
            ArgumentMatchers.eq(20),
            ArgumentMatchers.eq(1.0f),
            ArgumentMatchers.eq(135.0f),
            ArgumentMatchers.eq(13),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler, Mockito.times(3)).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }

    @Test
    @Throws(Exception::class)
    fun testParseGPGSA() {
//        val sentence = "\$GPGSA,A,3,25,02,26,05,29,31,21,12,,,,,1.6,1.0,1.3*3B"
        val sentence = "\$GNGSA,A,3,10,23,32,,,,,,,,,,2.5,2.3,1.0,1*37"
        BasicNMEAParser(handler).parse(sentence)
        Mockito.verify(handler).onStart()
        Mockito.verify(handler).onGSA(
            ArgumentMatchers.eq("A"),
            ArgumentMatchers.eq(FixType.Fix3D),
            ArgumentMatchers.anySet(),
//            ArgumentMatchers.argThat(
//                Helper.eq(
//                    HashSet(
//                        listOf(
//                            2,
//                            5,
//                            21,
//                            25,
//                            26,
//                            12,
//                            29,
//                            31
//                        )
//                    )
//                )
//
//            ) as Set<Int?>?,
            ArgumentMatchers.eq(1.6f),
            ArgumentMatchers.eq(1.0f),
            ArgumentMatchers.eq(1.3f),
            ArgumentMatchers.eq(false)
        )
        Mockito.verify(handler).onFinished()
        Mockito.verifyNoMoreInteractions(handler)
    }
}