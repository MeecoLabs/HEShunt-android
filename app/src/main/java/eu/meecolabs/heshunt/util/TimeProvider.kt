package eu.meecolabs.heshunt.util

import java.time.LocalDate

interface TimeProvider {
    fun now(): LocalDate
}
