package eu.meecolabs.heshunt.model

import java.time.LocalDate

data class CardWithStatus(
    val card: Card,
    val status: CardStatus
)

fun Card.withStatus(now: LocalDate): CardWithStatus =
    CardWithStatus(this, getStatus(now))

fun List<CardWithStatus>.sortedByStatus(): List<CardWithStatus> =
    sortedWith(
        compareBy<CardWithStatus> {
            when {
                !it.card.isCollected && it.status == CardStatus.ACTIVE -> 0
                !it.card.isCollected && it.status == CardStatus.UPCOMING -> 1
                it.card.isCollected -> 2
                !it.card.isCollected && it.status == CardStatus.EXPIRED -> 3
                else -> 4
            }
        }.thenBy { it.card.name }
    )
