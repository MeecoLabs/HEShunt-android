package eu.meecolabs.heshunt.util

import org.koin.core.annotation.Single
import java.time.LocalDate

@Single
class RealTimeProvider : TimeProvider {
    override fun now(): LocalDate =
        LocalDate.now()
}
