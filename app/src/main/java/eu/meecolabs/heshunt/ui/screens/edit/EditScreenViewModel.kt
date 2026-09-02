/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt.ui.screens.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.meecolabs.heshunt.model.Card
import eu.meecolabs.heshunt.model.Property
import eu.meecolabs.heshunt.repositories.CardRepository
import eu.meecolabs.heshunt.repositories.PropertyRepository
import eu.meecolabs.heshunt.usecase.GetCardsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel
import java.time.LocalDate
import java.time.ZoneId

internal sealed interface UiState {
    data object Loading : UiState

    data class Error(
        val message: String
    ) : UiState

    data class Success(
        val card: Card,
        val availableAt: List<Property>,
        val allSites: List<Property>
    ) : UiState
}

@KoinViewModel
class EditScreenViewModel(
    @InjectedParam private val cardId: String,
    getCardsUseCase: GetCardsUseCase,
    private val propertyRepository: PropertyRepository,
    private val repository: CardRepository
) : ViewModel() {
    private val _properties = MutableStateFlow<List<Property>>(emptyList())

    internal val uiState: StateFlow<UiState> = combine(
        getCardsUseCase(),
        _properties
    ) { cards, properties ->
        val card = cards.find { it.id == cardId }
        if (card == null) {
            UiState.Error("Card not found")
        } else {
            card.collectedOn?.let {
                _date.value = it
            }
            _property.value = card.collectedAt?.let { siteId -> properties.firstOrNull { it.id == siteId } }

            val availableAt = properties.filter { card.isAssociatedWith(it.id) }.sortedBy { it.name }
            val allSites = (properties - availableAt.toSet()).sortedBy { it.name }
            UiState.Success(card, availableAt, allSites)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    private val _date = MutableStateFlow<LocalDate>(LocalDate.now())
    val date = _date.asStateFlow()

    private val _property = MutableStateFlow<Property?>(null)
    val property = _property.asStateFlow()

    init {
        loadProperties()
    }

    private fun loadProperties() = viewModelScope.launch {
        try {
            _properties.value = propertyRepository.getProperties()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun setDate(value: LocalDate) {
        _date.value = value
    }

    fun setProperty(value: Property?) {
        _property.value = value
    }

    fun save(onDismiss: () -> Unit) = viewModelScope.launch {
        repository.updateCollected(
            cardId,
            _date.value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            _property.value
        )
        onDismiss()
    }
}
