package com.github.petr_s.nmea.basic

import kotlin.Throws
import java.lang.Exception
import com.github.petr_s.nmea.basic.BasicNMEAHandler.FixQuality
import com.github.petr_s.nmea.basic.BasicNMEAHandler.FixType
import java.io.UnsupportedEncodingException
import java.lang.Class
import java.lang.StringBuilder
import kotlin.jvm.Synchronized
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class BasicNMEAParser(private val handler: BasicNMEAHandler?) {
    companion object {
        private const val KNOTS2MPS = 0.514444f
        private val TIME_FORMAT = SimpleDateFormat("HHmmss", Locale.US)
        private val DATE_FORMAT = SimpleDateFormat("ddMMyy", Locale.US)
        private const val COMMA = ","
        private const val CAP_FLOAT = "(\\d*[.]?\\d+)"
        private const val CAP_NEGATIVE_FLOAT = "([-]?\\d*[.]?\\d+)"
        private const val HEX_INT = "[0-9a-fA-F]"
        private val GENERAL_SENTENCE = Pattern.compile("^\\$(\\w{5}),(.*)[*](" + HEX_INT + "{2})$")
        private val GPRMC = Pattern.compile(
            "(\\d{5})?" +
                    "(\\d[.]?\\d*)?" + COMMA +
                    regexify(Status::class.java) + COMMA +
                    "(\\d{2})(\\d{2}[.]\\d+)?" + COMMA +
                    regexify(VDir::class.java) + "?" + COMMA +
                    "(\\d{3})(\\d{2}[.]\\d+)?" + COMMA +
                    regexify(HDir::class.java) + "?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    "(\\d{6})?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    regexify(HDir::class.java) + "?" + COMMA + "?" +
                    regexify(FFA::class.java) + "?" + COMMA + "?" +
                    "([A-Z]+)?"
        )
        private val GPGGA = Pattern.compile(
            "(\\d{5})?" +
                    "(\\d[.]?\\d*)?" + COMMA +
                    "(\\d{2})(\\d{2}[.]\\d+)?" + COMMA +
                    regexify(VDir::class.java) + "?" + COMMA +
                    "(\\d{3})(\\d{2}[.]\\d+)?" + COMMA +
                    regexify(HDir::class.java) + "?" + COMMA +
                    "(\\d)?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    CAP_FLOAT + "?,[M]" + COMMA +
                    CAP_NEGATIVE_FLOAT + "?,[M]" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    "(\\d{4})?"
        )
        private val GPGSV = Pattern.compile(
            "(\\d+)" + COMMA +
                    "(\\d+)" + COMMA +
                    "(\\d{2})" + COMMA +
                    "(\\d{2})" + COMMA +
                    "(\\d{2})" + COMMA +
                    "(\\d{3})" + COMMA +
                    "(\\d{2})" + COMMA +
                    "(\\d{2})?" + COMMA + "?" +
                    "(\\d{2})?" + COMMA + "?" +
                    "(\\d{3})?" + COMMA + "?" +
                    "(\\d{2})?" + COMMA + "?" +
                    "(\\d{2})?" + COMMA + "?" +
                    "(\\d{2})?" + COMMA + "?" +
                    "(\\d{3})?" + COMMA + "?" +
                    "(\\d{2})?" + COMMA + "?" +
                    "(\\d{2})?" + COMMA + "?" +
                    "(\\d{2})?" + COMMA + "?" +
                    "(\\d{3})?" + COMMA + "?" +
                    "(\\d{2})?"
        )
        private val GPGSA = Pattern.compile(
            regexify(
                Mode::class.java
            ) + COMMA +
                    "(\\d)" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    CAP_FLOAT + "?"
        )

        private val GPGSA2 = Pattern.compile(
            regexify(
                Mode::class.java
            ) + COMMA +
                    "(\\d)" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    "(\\d{2})?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    CAP_FLOAT + "?" + COMMA +
                    "(\\d)?"
        )
        private val functions = HashMap<StringType, ParsingFunction>()
        @Throws(Exception::class)
        private fun parseGPRMC(
            handler: BasicNMEAHandler?,
            sentence: String,
            type: StringType
        ): Boolean {
            val matcher = ExMatcher(GPRMC.matcher(sentence))
            if (matcher.matches()) {
                val isGN = type == StringType.GNRMC
                var time = TIME_FORMAT.parse(matcher.nextString("time") + "0").time
                val ms = matcher.nextFloat("time-ms")
                if (ms != null) {
                    time += (ms * 1000).toLong()
                }
                val status = Status.valueOf(matcher.nextString("status")!!)
                if (status == Status.A) {
                    val latDeg = matcher.nextInt("degrees")
                    val latMin = matcher.nextFloat("minutes")
                    val latitude = if(latDeg != null && latMin != null) toDegrees(latDeg, latMin) else null

                    val vDir = matcher.nextString("vertical-direction")?.let { VDir.valueOf(it) }


                    val lonDeg = matcher.nextInt("degrees")
                    val lonMin = matcher.nextFloat("minutes")
                    val longitude = if(lonDeg != null && lonMin != null) toDegrees(lonDeg, lonMin) else null

                    val hDir = matcher.nextString("horizontal-direction")?.let { HDir.valueOf(it) }
                    val speed = matcher.nextFloat("speed")?.times(KNOTS2MPS)
                    val direction = matcher.nextFloat("direction", 0.0f)
                    val date = matcher.nextString("date")?.let { DATE_FORMAT.parse(it).time }
                    val magVar = matcher.nextFloat("magnetic-variation")
                    val magVarDir = matcher.nextString("direction")

                    /*
                 * Positioning system mode indicator
                 *
                 */
                    val modeIndicator = matcher.nextString("faa")
                    handler!!.onRMC(
                        date,
                        time,
                        status.toString(),
                        if(latitude != null && vDir != null) { if (vDir == VDir.N) latitude else -latitude } else null,
                        if(longitude != null && hDir != null) { if (hDir == HDir.E) longitude else -longitude } else null,
                        speed,
                        direction,
                        magVar,
                        magVarDir,
                        modeIndicator,
                        isGN
                    )
                } else {
                    handler!!.onRMC(
                        null,
                        time,
                        status.toString(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        isGN
                    )
                }
                return true
            }
            return false
        }

        @Throws(Exception::class)
        private fun parseGPGGA(
            handler: BasicNMEAHandler?,
            sentence: String,
            type: StringType
        ): Boolean {
            val matcher = ExMatcher(GPGGA.matcher(sentence))
            if (matcher.matches()) {
                val isGN = type == StringType.GNGGA
                var time = TIME_FORMAT.parse(matcher.nextString("time") + "0").time
                val ms = matcher.nextFloat("time-ms")
                if (ms != null) {
                    time += (ms * 1000).toLong()
                }

                val latDeg = matcher.nextInt("degrees")
                val latMin = matcher.nextFloat("minutes")

                val latitude = if(latDeg != null && latMin != null) toDegrees(latDeg, latMin) else null

                val vDir = matcher.nextString("vertical-direction")?.let { VDir.valueOf(it) }

                val lonDeg = matcher.nextInt("degrees")
                val lonMin = matcher.nextFloat("minutes")
                val longitude = if(lonDeg != null && lonMin != null) toDegrees(lonDeg, lonMin) else null

                val hDir = matcher.nextString("horizontal-direction")?.let { HDir.valueOf(it) }
                val quality = matcher.nextInt("quality")?.let { FixQuality.values()[it] }
                val satellites = matcher.nextInt("n-satellites")
                val hdop = matcher.nextFloat("hdop")
                val altitude = matcher.nextFloat("altitude")
                val separation = matcher.nextFloat("separation")
                val age = matcher.nextFloat("age")
                val station = matcher.nextInt("station")
                handler!!.onGGA(
                    time,
                    if(latitude != null && vDir != null) { if (vDir == VDir.N) latitude else -latitude } else null,
                    if(longitude != null && hDir != null) { if (hDir == HDir.E) longitude else -longitude } else null,
                    if(altitude != null && separation != null) altitude - separation else null,
                    quality,
                    satellites,
                    hdop,
                    age,
                    station,
                    isGN
                )
                return true
            }
            return false
        }

        @Throws(Exception::class)
        private fun parseGPGSV(
            handler: BasicNMEAHandler?,
            sentence: String,
            type: StringType
        ): Boolean {
            val matcher = ExMatcher(GPGSV.matcher(sentence))
            if (matcher.matches()) {
                val isGN = type == StringType.GNGSV
                val sentences = matcher.nextInt("n-sentences")
                val index = matcher.nextInt("sentence-index")?.minus(1)
                val satellites = matcher.nextInt("n-satellites")
                for (i in 0..3) {
                    val prn = matcher.nextInt("prn")
                    val elevation = matcher.nextInt("elevation")
                    val azimuth = matcher.nextInt("azimuth")
                    val snr = matcher.nextInt("snr")
                    if (prn != null) {
                        handler!!.onGSV(
                            satellites,
                            index?.times(4)?.plus(i),
                            prn,
                            elevation?.toFloat(),
                            azimuth?.toFloat(),
                            snr,
                            isGN
                        )
                    }
                }
                return true
            }
            return false
        }

        private fun parseGPGSA(
            handler: BasicNMEAHandler?,
            sentence: String,
            stringType: StringType
        ): Boolean {
            var matcher = ExMatcher(GPGSA.matcher(sentence))
            if (matcher.matches()) {
                val isGN = stringType == StringType.GNGSA

                /*
             * A = Automatic 2D/3D
             * M = Manual, forced to operate in 2D or 3D
             */
                val mode = matcher.nextString("mode")?.let { Mode.valueOf(it) }
                val type = matcher.nextInt("fix-type")?.let { FixType.values()[it] }
                val prns: MutableSet<Int?> = HashSet()
                for (i in 0..11) {
                    matcher.nextInt("prn")?.let {
                        prns.add(it)
                    }
                }
                val pdop = matcher.nextFloat("pdop")
                val hdop = matcher.nextFloat("hdop")
                val vdop = matcher.nextFloat("vdop")
                handler!!.onGSA(mode.toString(), type, prns, pdop, hdop, vdop, isGN)
                return true
            }

            matcher = ExMatcher(GPGSA2.matcher(sentence))
            if (matcher.matches()) {
                val isGN = stringType == StringType.GNGSA

                /*
             * A = Automatic 2D/3D
             * M = Manual, forced to operate in 2D or 3D
             */
                val mode = matcher.nextString("mode")?.let { Mode.valueOf(it) }
                val type = matcher.nextInt("fix-type")?.let { FixType.values()[it] }
                val prns: MutableSet<Int?> = HashSet()
                for (i in 0..11) {
                    matcher.nextInt("prn")?.let {
                        prns.add(it)
                    }
                }
                val pdop = matcher.nextFloat("pdop")
                val hdop = matcher.nextFloat("hdop")
                val vdop = matcher.nextFloat("vdop")
                val systemID = matcher.nextInt("systemID")
                //TODO parse systemID
                handler!!.onGSA(mode.toString(), type, prns, pdop, hdop, vdop, isGN)
                return true
            }

            return false
        }

        @Throws(UnsupportedEncodingException::class)
        private fun calculateChecksum(sentence: String): Int {
            val bytes = sentence.substring(1, sentence.length - 3).toByteArray(charset("US-ASCII"))
            var checksum = 0
            for (b in bytes) {
                checksum = checksum xor b.toInt()
            }
            return checksum
        }

        private fun toDegrees(degrees: Int, minutes: Float): Double {
            return degrees + minutes / 60.0
        }

        private fun <T : Enum<T>?> regexify(clazz: Class<T>): String {
            val sb = StringBuilder()
            sb.append("([")
            for (c in clazz.enumConstants) {
                sb.append(c.toString())
            }
            sb.append("])")
            return sb.toString()
        }

        init {
            TIME_FORMAT.timeZone = TimeZone.getTimeZone("UTC")
            DATE_FORMAT.timeZone = TimeZone.getTimeZone("UTC")
            functions[StringType.GPRMC] = object : ParsingFunction() {
                @Throws(Exception::class)
                override fun parse(handler: BasicNMEAHandler?, sentence: String): Boolean {
                    return parseGPRMC(handler, sentence, StringType.GPRMC)
                }
            }
            functions[StringType.GNRMC] = object : ParsingFunction() {
                @Throws(Exception::class)
                override fun parse(handler: BasicNMEAHandler?, sentence: String): Boolean {
                    return parseGPRMC(handler, sentence, StringType.GNRMC)
                }
            }
            functions[StringType.GPGGA] = object : ParsingFunction() {
                @Throws(Exception::class)
                override fun parse(handler: BasicNMEAHandler?, sentence: String): Boolean {
                    return parseGPGGA(handler, sentence, StringType.GPGGA)
                }
            }
            functions[StringType.GNGGA] = object : ParsingFunction() {
                @Throws(Exception::class)
                override fun parse(handler: BasicNMEAHandler?, sentence: String): Boolean {
                    return parseGPGGA(handler, sentence, StringType.GNGGA)
                }
            }
            functions[StringType.GPGSV] = object : ParsingFunction() {
                @Throws(Exception::class)
                override fun parse(handler: BasicNMEAHandler?, sentence: String): Boolean {
                    return parseGPGSV(handler, sentence, StringType.GPGSV)
                }
            }
            functions[StringType.GNGSV] = object : ParsingFunction() {
                @Throws(Exception::class)
                override fun parse(handler: BasicNMEAHandler?, sentence: String): Boolean {
                    return parseGPGSV(handler, sentence, StringType.GNGSV)
                }
            }
            functions[StringType.GPGSA] = object : ParsingFunction() {
                @Throws(Exception::class)
                override fun parse(handler: BasicNMEAHandler?, sentence: String): Boolean {
                    return parseGPGSA(handler, sentence, StringType.GPGSA)
                }
            }
            functions[StringType.GNGSA] = object : ParsingFunction() {
                @Throws(Exception::class)
                override fun parse(handler: BasicNMEAHandler?, sentence: String): Boolean {
                    return parseGPGSA(handler, sentence, StringType.GNGSA)
                }
            }
        }
    }

    init {
        if (handler == null) {
            throw NullPointerException()
        }
    }

    @Synchronized
    fun parse(sentence: String?) {
        if (sentence == null) {
            throw NullPointerException()
        }
        handler!!.onStart()
        try {
            val matcher = ExMatcher(GENERAL_SENTENCE.matcher(sentence))
            if (matcher.matches()) {
                val type = matcher.nextString("type")!!
                val stringType = StringType.valueOf(type)
                val content = matcher.nextString("content")!!
                val expected_checksum = matcher.nextHexInt("checksum")!!
                val actual_checksum = calculateChecksum(sentence)
                if (actual_checksum != expected_checksum) {
                    handler.onBadChecksum(expected_checksum, actual_checksum)
                } else if (!functions.containsKey(stringType) || !functions[stringType]!!
                        .parse(
                            handler, content
                        )
                ) {
                    handler.onUnrecognized(sentence)
                }
            } else {
                handler.onUnrecognized(sentence)
            }
        } catch (e: Exception) {
            handler.onException(e)
        } finally {
            handler.onFinished()
        }
    }

    private enum class Status {
        A, V
    }

    private enum class HDir {
        E, W
    }

    private enum class VDir {
        N, S
    }

    private enum class Mode {
        A, M
    }

    private enum class FFA {
        A, D, E, M, S, N
    }

    private enum class StringType {
        GPGGA, GPRMC, GPGSV, GPGSA, GNGGA, GNRMC, GNGSV, GNGSA
    }

    private abstract class ParsingFunction {
        @Throws(Exception::class)
        abstract fun parse(handler: BasicNMEAHandler?, sentence: String): Boolean
    }

    private class ExMatcher(var original: Matcher) {
        var index = 0

        init {
            reset()
        }

        fun reset() {
            index = 1
        }

        fun matches(): Boolean {
            return original.matches()
        }

        fun nextString(name: String?): String? {
            return original.group(index++)
        }

        fun nextFloat(name: String?, defaultValue: Float): Float {
            val next = nextFloat(name)
            return next ?: defaultValue
        }

        fun nextFloat(name: String?): Float? {
            return nextString(name)?.toFloat()
        }

        fun nextInt(name: String?): Int? {
            return nextString(name)?.toInt()
        }

        fun nextHexInt(name: String?): Int? {
            return nextString(name)?.toInt(16)
        }
    }
}