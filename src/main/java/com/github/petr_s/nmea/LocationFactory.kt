package com.github.petr_s.nmea

import android.location.Location

abstract class LocationFactory {
    abstract fun newLocation(): Location?
}